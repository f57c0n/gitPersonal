import sqlite3
import os
import pandas as pd
from dateutil import parser
from datetime import datetime as dt
from sys import exit as systemexit

# see UML for additional app documentation
# GLOBAL VARIABLES
# keeps track of which database is being used
db = 'db/dbtesttest.db'

# creates a database if it doesn't exist and connect to it.
# ORACLE:
# connection = ora.connect()
# SQLLITE:
connection = sqlite3.connect(db)

# creates a cursor to execute sql statements
cursor = connection.cursor()

# stores a dictionary of file details
argDict = {}

# specifies default date format
date_format = 'YYYY-MM-DD HH24:MI:SS.FF'

# stores table column names and its data type
header_names = ()
header_types = ()
char_length = 0

# stores the first few # lines of the data, its header and detected type
first_data_tuple = ()
file_headers = ()
file_types = ()
file_type_stats = []

# dictionary of header names and types may not be needed if it does't facilitate anything
colDict = {}

#  valid data types
# ORACLE:
# valid_types = ['VARCHAR2', 'VARCHAR', 'NUMBER', 'DATE', 'TIMESTAMP']
# SQLLITE:
valid_types = ['VARCHAR()', 'TEXT', 'REAL', 'INTEGER']


def main():
    # sets the character coding to be used.
    # os.environ["NLS_LANG"] = "American_America.UTF8"
    os.environ["NLS_LANG"] = "American_America.WE8MSWIN1252"

    # Current script's directory
    this_dir = os.path.dirname(os.path.realpath(__file__))

    # change current working directory to script's directory
    os.chdir(this_dir)

    while True:
        print("Select Option: ")
        print("S:  Start Migration ")
        print("Q:  Exit Application")
        print("A:  About")
        menuOption = input("Enter Option: ")

        if menuOption.upper() == str('S'):
            getFile()
            displayTableMenu()
        elif menuOption.upper() == 'Q':
            quitApp()
        elif menuOption.upper() == 'A':
            about()
        else:
            print("Invalid Option, Try again")


def quitApp():
    cursor.close()
    connection.close()
    print("CONNECTION CLOSED")
    systemexit()


# PROVIDES OPTION TO RETRY MENU OPTIONS
# TO DO: refactor and make options dictionary
def tryAgain(flag):
    text = ''
    if flag.upper() == 'T':
        text = "TABLE DOESN'T EXIST. TRY AGAIN (Y/N): "
    elif flag.upper() == 'H':
        text = "Does file contain header. (Y/N): "
    elif flag.upper() == 'E':
        text = "TABLE ALREADY EXIST. TRY AGAIN (Y/N): "
    elif flag.upper() == 'U':
        text = "Use this as column names.  (Y/N): "
    elif flag.upper() == 'A':
        text = "Accept. (Y/N): "
    elif flag.upper() == 'P':
        text = "Proceed reading and inserting input file. (Y/N): "
    elif flag.upper() == 'D':
        text = "Proceed dropping table. (Y/N): "
    elif flag.upper() == 'C':
        text = "Permanently commit changes to DB. (Y/N): "

    allowedAns = ['Y', 'N', 'y', 'n']
    option = input(text)
    while len(option) != 1 or option not in allowedAns:
        option = input(text)

    if option.upper() == 'Y':
        return True
    else:
        return False


# VALIDATES SEPARATOR
def checkSeparator():
    option = input("Enter File Separator: ")
    while len(option) != 1 or option.isalnum():
        option = input("SEPARATOR MUST BE A SINGLE CHARACTER AND NOT ALPHANUMERIC: ")

    return option


# GETS FILE DETAILS
def getFile():
    print("\nGETTING FILE DETAILS")
    argDict['file'] = input("Enter Filename: ")
    while not os.path.isfile(argDict['file']):
        argDict['file'] = input("Enter Valid Filename: ")

    argDict['sep'] = checkSeparator()
    argDict['header'] = tryAgain('h')
    print("DATE FORMAT DEFAULT:  ", date_format)
    argDict['dateFormat'] = input("Enter Date Format on the table's DATE TYPED columns or Blank to accept default: ")

    # USE DEFAULT IF DATE FORMAT IS NOT PROVIDED
    if argDict['dateFormat'].isspace() or not argDict['dateFormat']:
        argDict['dateFormat'] = date_format


# PROVIDES OPTIONS TO EITHER OVERWRITE, APPEND OR CREATE NEW TABLE
def displayTableMenu():
    # MAYBE REMOVED ONCE DEFAULT IS CONFIRMED
    if argDict['dateFormat'] == "":
        text = 'NONE'
    else:
        text = argDict['dateFormat']

    print("\nFILE DETAILS: Filename: ", argDict['file'], ', separated by: ', argDict['sep'], ', with header: ',
          argDict['header'], ", DATE FORMAT to be used: ", text)

    # TAKES A PEEK AT THE INPUT DATA ONLY ONCE AND SAVE IT
    peekData()

    while True:
        print("\nTABLE MENU - Select Option: ")
        print("1:  Overwrite Existing Table ")
        print("2:  Append to Existing Table  ")
        print("3:  Create and Insert to New Table ")
        print("4:  Drop Existing Table ")
        print("5:  List Existing Tables ")
        print("6:  Get File Column Stats ")
        print("0:  Start Another Migration")
        print("Q:  Exit Application")
        menuOption = input("Enter Option: ")

        if menuOption == str('1'):
            overwriteTable()
        elif menuOption == str('2'):
            appendTable()
        elif menuOption == str('3'):
            prepareTable()
        elif menuOption == str('4'):
            dropTable()
        elif menuOption == str('5'):
            getTableList()
        elif menuOption == str('6'):
            try:
                rows = int(input("Enter number of rows for sampling: "))
                if rows > 0:
                    getColumnStats(True, rows)
                else:
                    print("Enter a valid positive integer!")
            except ValueError:
                print("Enter a valid positive integer!")
        elif menuOption == str('0'):
            break
        elif menuOption.upper() == 'Q':
            quitApp()
        else:
            print("Invalid Option, Try again")


# PEEKS AT INPUT FILE AND RETRIEVE FIRST ROW
def peekData():
    global file_headers
    global file_types
    global first_data_tuple

    # retrieves header where applicable and first line of data
    if argDict['header']:
        file_headers = tuple(pd.read_csv(argDict['file'], sep=argDict['sep'], nrows=0).columns)
        df = pd.read_csv(argDict['file'], sep=argDict['sep'], nrows=1, skiprows=0, error_bad_lines=False)
        first_data_tuple = [row_tuple for row_tuple in df.itertuples(index=False, name=None)][0]
    else:
        df = pd.read_csv(argDict['file'], sep=argDict['sep'], header=None, nrows=1, error_bad_lines=False)
        first_data_tuple = [row_tuple for row_tuple in df.itertuples(index=False, name=None)][0]

    # USE THE STATS REPORT TO RECOMMEND TYPE
    # TO DO:  use % or user input
    file_types = tuple(getColumnStats(False, 50))


# DISPLAYS PEEK DATA TO USE AS A REFERENCE TO THE DATA TYPES
def displayPeekData():
    if argDict['header']:
        header = "HEADERS: "
    else:
        header = "NO HEADERS: "

    print("YOUR FILE CONTAINS: ")
    print(header, file_headers)
    print("DETECTED DATA TYPE: ", file_types)
    print("FIRST LINE OF DATA: ", first_data_tuple)
    print("\n")


# TO DO:  COMBINE CODE WITH APPEND, REFACTOR INTO SINGLE FUNCTION
# OVERWRITES TABLE
def overwriteTable():
    while True:
        argDict['table'] = input("Enter Existing Table Name: ")
        if checkTableExist():
            print("Deleting all entries in the table")
            if truncateTable():
                getColumns('E')
                displayPeekData()
                if len(file_headers) == len(header_names) and tryAgain('P'):
                    readFile()
                    break
                else:
                    if len(file_headers) != len(header_names):
                        print("Number of table columns and file columns do not match!")
                        print("WILL NOT PROCEED WITH INSERT")
                    break
            else:
                break
        else:
            if not tryAgain('T'):
                break


# APPENDS TO EXISTING TABLE
def appendTable():
    while True:
        argDict['table'] = input("Enter Existing Table Name: ")
        if checkTableExist():
            getColumns('E')
            displayPeekData()
            if len(file_headers) == len(header_names) and tryAgain('P'):
                readFile()
                break
            else:
                if len(file_headers) != len(header_names):
                    print("Number of table columns and file columns do not match!")
                    print("WILL NOT PROCEED WITH INSERT")
                break
        else:
            if not tryAgain('t'):
                break


# DROPS TABLE IF CREATED IN ERROR.  MUST BE CAUTIOUS AS TO NOT DELETE UNINTENDED TABLES
def dropTable():
    while True:
        argDict['table'] = input("Enter Existing Table Name: ")
        if checkTableExist():
            print("DROPPING TABLE: ", argDict['table'])
            if tryAgain('D'):
                try:
                    # ORACLE
                    # sql = 'DROP TABLE' + ' ' + argDict['table'] + 'PURGE'
                    # SQLLITE
                    sql = 'DROP TABLE' + ' ' + argDict['table']
                    cursor.execute(sql)
                    # AUTO COMMITS
                except sqlite3.Error as err:
                    print("Database Error: %s" % err)
                break
            else:
                break
        else:
            if not tryAgain('T'):
                break


# CHECKS IF GIVEN TABLE ALREADY EXIST
def checkTableExist():
    # ORACLE:
    # sql =
    # SQLLITE:
    sql = 'SELECT name FROM sqlite_master WHERE type = "table" AND name = "%s" ' % argDict['table']

    cursor.execute(sql)

    return cursor.fetchone()


# DELETES ALL ENTRY FROM GIVEN TABLE, IF IT EXIST
def truncateTable():
    try:
        # ORACLE:
        # sql = 'TRUNCATE TABLE ' + argDict['table']

        # SQLLITE:
        sql = 'DELETE FROM' + ' ' + argDict['table']
        cursor.execute(sql)
        print("ALL ROWS DELETED !!!")
    # EXISTENCE CHECKED WHEN TABLE NAME ENTERED
    except Exception as e:
        print("Error: " + str(e))

    if tryAgain('C'):
        connection.commit()
        return True
    else:
        connection.rollback()

    return False


# CREATES NEW TABLE ONLY AFTER GETTING COLUMNS
def prepareTable():
    # PEEKS AT DATA AS A REFERENCE TO THE DATA TYPES
    displayPeekData()

    # validate table name = begins with a letter, no more than 30 chars, not keyword, $ _ # only
    print("TO DO:  VALIDATE table name with special characters")
    while True:
        argDict['table'] = input("Enter New Table Name: ")
        while 1 > len(argDict['table']) > 20 or argDict['table'] == '' or not argDict['table'][0].isalpha():
            argDict['table'] = input('Enter Valid Table Name  [1 to 20 characters, starts with letter]: ')

        if not checkTableExist():
            ready = displayColumnMenu()
            if ready and colDict:
                if createTable() and tryAgain('P'):
                    readFile()
                    break
                else:
                    break
            else:
                print("NO TABLE CREATED")
                print("MUST SELECT COLUMNS BEFORE CREATING TABLE")
                break
        else:
            if not tryAgain('E'):
                break


# RETRIEVES COLUMNS NAMES AND ITS DATA TYPE FROM EXISTING TABLE
# TO DO:  refactor and condense code
def getColumns(flag):
    global colDict
    global header_names
    global header_types

    text = "YOUR CHOSEN COLUMNS: "
    ready = False

    # USE EXISTING TABLE COLUMN NAME AND TYPE
    if flag.upper() == 'E':
        # ORACLE:
        # sql = select_statement = "select column_name, data_type from user_tab_columns where table_name = '" + \
        #                          argDict['table'].upper() + "' order by column_id"

        # SQLLITE:
        sql = 'PRAGMA table_info(%s)' % argDict['table']
        cursor.execute(sql)

        columnTuple = cursor.fetchall()

        if columnTuple:
            # stores column name and type into column dictionary
            header_names = tuple([a[1] for a in columnTuple])
            header_types = tuple([a[2] for a in columnTuple])
            colDict = {header_names[i]: header_types[i] for i in range(len(header_names))}
            ready = True
    # USE BOTH RECOMMENDED NAMES AND TYPES
    elif flag.upper() == 'OPTION1':
        tempDict = {file_headers[i]: file_types[i] for i in range(len(file_headers))}

        print(text, tempDict)
        if tryAgain('A'):
            header_names = file_headers
            header_types = file_types
            colDict = tempDict
            ready = True
        else:
            print("TO DO:  option to edit")
    # USE NAME AND GET TYPE
    elif flag.upper() == 'OPTION2':
        # get user input for column type
        col_types = getColType(file_headers)
        tempDict = {file_headers[i]: col_types[i] for i in range(len(file_headers))}

        print(text, tempDict)
        if tryAgain('A'):
            header_names = file_headers
            header_types = tuple(col_types)
            colDict = tempDict
            ready = True
        else:
            print("TO DO:  option to edit")
    # USE TYPE AND GET NAME
    elif flag.upper() == 'OPTION3':
        # get user input for column names
        col_names = getColName(file_types)
        tempDict = {col_names[i]: file_types[i] for i in range(len(file_types))}

        print(text, tempDict)
        if tryAgain('A'):
            header_names = tuple(col_names)
            header_types = file_types
            colDict = tempDict
            ready = True
        else:
            print("TO DO:  option to edit")
    # USE NEITHER.  GET NAME AND TYPE
    elif flag.upper() == 'OPTION4':
        # Test for headers, then option to use it or create new ones
        print("Enter Column Names and corresponding Data Type")

        col_names = getColName(file_headers)
        col_types = getColType(col_names)

        tempDict = {col_names[i]: col_types[i] for i in range(len(col_names))}

        print(text, tempDict)
        if tryAgain('A'):
            header_names = tuple(col_names)
            header_types = tuple(col_types)
            colDict = tempDict
            ready = True
        else:
            print("TO DO:  option to edit")

    # displays back to user the new column names/types
    print("COLUMNS IN {}: ".format(argDict['table']))
    for name, data_type in colDict.items():
        print(name, '\t', data_type)

    return ready


# PROVIDES MENU OPTION TO DETERMINE COLUMNS
def displayColumnMenu():
    ready = False
    while True:
        print("\nSelect Option To Determine Columns: ")
        print("1:  Use BOTH detected column names and types ")
        print("2:  Use detected column name but enter column type  ")
        print("3:  Enter column name but used detected column type ")
        print("4:  Enter BOTH column names and types")
        print("5:  Get column type statistics")
        print("0:  Exit Without Inserting Data")
        print("Q:  Exit Application")
        menuOption = input("Enter Option: ")

        # checks if file headers exist, else option won't proceed
        if menuOption == str('1'):
            if file_headers:
                ready = getColumns('OPTION1')
                if ready:
                    break
            else:
                print("FILE HAS NO HEADER")
        elif menuOption == str('2'):
            if file_headers:
                ready = getColumns('OPTION2')
                if ready:
                    break
            else:
                print("FILE HAS NO HEADER")
        elif menuOption == str('3'):
            if file_types:
                ready = getColumns('OPTION3')
                if ready:
                    break
            else:
                print("FILE HAS NO HEADER TYPES")
        elif menuOption == str('4'):
            ready = getColumns('OPTION4')
            if ready:
                break
        elif menuOption == str('5'):
            try:
                rows = int(input("Enter number of rows for sampling: "))
                if rows > 0:
                    getColumnStats(True, rows)
                else:
                    print("Enter a valid positive integer!")
            except ValueError:
                print("Enter a valid positive integer!")
        elif menuOption == str('0'):
            break
        elif menuOption.upper() == 'Q':
            quitApp()
        else:
            print("Invalid Option, Try again")

    return ready


# DETECTS THE DATA TYPE OF THE INPUT FIELDS, NEEDED TO CREATE NEW TABLE
# TO DO:  INCLUDE ALL NUANCES OF THE DATA TYPES.
# IE) INTEGER VS REAL
# IE) OPTION TO SPECIFY THE LENGTH OF VARCHAR(n)
def detectInputType(val):
    # ORACLE TYPES:  VARCHAR(20), NUMBER, BINARY_FLOAT OR DOUBLE, DATE, TIMESTAMP
    # date = "DATE"
    # SQLLITE:  DOESN'T HAVE DATE AND VARCHAR() DOES NOT CONSIDER LENGTH, BASICALLY THE SAME AS TEXT
    date_type = "TEXT"
    val = str(val).strip()

    # try number
    if val != '' or val is not None:
        try:
            float(val.replace(',', '').replace(' ', ''))
            return ("REAL", len(val))
        except ValueError:
            pass

    # try date
    if not val or val is not None:
        try:
            parser.parse(val)
            return (date_type, len(val))
        except ValueError:
            pass
        except TypeError:
            pass

    # otherwise string
    return (val, 0) and ("VARCHAR()", len(val))


# GETS USER INPUT FOR COLUMN NAMES
def getColName(data_types):
    num = len(data_types)
    col_keys = []
    for i in range(num):
        this_key = (input('Enter Column Name ' + str(i + 1) + ': ')).upper()
        while 1 > len(this_key) > 20 or this_key == '' or this_key in col_keys or not this_key[0].isalpha():
            this_key = (input('Enter Valid Column Name ' + str(
                i + 1) + ' [1 to 20 characters, begins with a letter, no duplicates]: ')).upper()
        col_keys.append(this_key)

    return col_keys


# GETS USER INPUT FOR COLUMN TYPE
def getColType(names):
    num = len(names)
    col_types = []
    print("VALID DATA TYPES: ", valid_types)
    for i in range(num):
        this_type = (input('Enter Column Data Type for ' + names[i] + ': ')).upper()
        while this_type not in valid_types:
            this_type = (input('Enter Valid Column Data Type for ' + names[i] + ': ')).upper()
        col_types.append(this_type)

    return col_types


# CREATE SQL TABLE
def createTable():
    text = ",\n".join("'{}' {}".format(header_names[i], header_types[i]) for i, val in enumerate(header_types))

    if colDict and argDict['table']:
        sql = '''CREATE TABLE IF NOT EXISTS {} 
                        ({})'''.format(argDict['table'], text)
        try:
            cursor.execute(sql)
            print(sql)
            print("TABLE CREATED")
            return True
            # AUTO COMMITS IN SQLLITE AND ORACLE
        except sqlite3.Error as err:
            print("Database Error: %s" % err)
            print('TABLE NOT CREATED')
    else:
        print('CAN NOT CREATE TABLE.  TABLE COLUMNS NOT VALID')

    return False


# READS THE DELIMITED FILE
def readFile():
    print("READING FILE")
    numCol = len(header_types)
    col_numbers = [i for i in range(0, numCol)]
    current_row_num = 0
    success_count = 0
    fail_count = 0
    has_error = False
    read_error_list = []
    sql_error_msg = []
    error_msg = []

    # TO DO:  user input for chunk size
    # Process data in chunks.  chunk is a dataframe
    chunksize = 20

    if argDict['header']:
        header = 0
    else:
        header = None

    # read up on infer_datetime format
    for chunk in pd.read_csv(argDict['file'], header=header, sep=argDict['sep'], chunksize=chunksize,
                             float_precision='round_trip', usecols=col_numbers, quoting=3,
                             error_bad_lines=False, encoding='cp1252'):

        # replaces "nan" from panda with empty string
        chunk = chunk.fillna('')

        print("INSERTING BATCH OF DATA")
        for row_tuple in chunk.itertuples(index=False, name=None):
            current_row_num += 1
            # Insert row of data into table
            try:
                # SQLLITE:
                sql = 'INSERT INTO {} {}\nVALUES ({})'.format(argDict['table'], header_names,
                                                              ", ".join(map(convert, row_tuple, header_types)))
            # catches python error on data type detection
            except ValueError as e:
                has_error = True
                fail_count += 1
                msg = ': '.join(['ROW ' + str(current_row_num), str(e)])
                error_msg.append(msg)
                read_error_list.append(row_tuple)
            else:
                try:
                    # print(sql)
                    cursor.execute(sql)
                    success_count += 1
                # catches sql errors on the actual insertion of data
                except sqlite3.Error as err:
                    has_error = True
                    fail_count += 1
                    msg = ': '.join(['ROW ' + str(current_row_num), str(err)])
                    sql_error_msg.append(msg)
                    read_error_list.append(row_tuple)
                    print("Database Error: %s" % err)

    print("{}: {:.1f}% LINES LOADED.".format(success_count, (success_count * 100) / current_row_num))

    if has_error:
        print("{}: {:.1f}% ERRORS FOUND.".format(fail_count, (fail_count * 100) / current_row_num))
        print("Exporting Invalid Entries and Messages to a file.")
        saveInvalidData(read_error_list)
        saveReadErrorMsg(error_msg)
        saveReadErrorMsg(sql_error_msg)
    else:
        print("No Errors encountered.")

    if tryAgain('C'):
        connection.commit()
    else:
        connection.rollback()


# SAVES/EXPORT LINES WITH ERRORS TO A CSV FILE
# TO DO:  zip file if too big
def saveInvalidData(error_list):
    # attaches a timestamp to the filename
    text = str(dt.now()).strip()
    filename = '-'.join(['errorInvalids', text])

    # via Panda
    df = pd.DataFrame(error_list)
    df.to_csv(filename, sep='|', index=False, header=header_names)


# SAVES/EXPORT ERROR MESSAGES TO FILE
def saveReadErrorMsg(error_list):
    if error_list:
        # attaches a timestamp to the filename
        text = str(dt.now()).strip()
        filename = ': '.join(['errorMessages', text])
        with open(filename, "w") as outfile:
            outfile.write("\n".join(str(line) for line in error_list))

        outfile.close()


# PROVIDES STATS REPORT ON DATA TYPE OF ALL COLUMNS
def getColumnStats(display, rows):
    # rows is a user input value to determine size of sample data
    # else it is hardcoded for data type recommendation use
    global file_type_stats
    global char_length

    max_list = []

    if argDict['header']:
        header = 0
    else:
        header = None

    file_type_stats = [{data_type: 0 for data_type in valid_types} for x in first_data_tuple]
    file_type_length = [{'data_length': 0} for x in first_data_tuple]

    df = pd.read_csv(argDict['file'], sep=argDict['sep'], header=header, nrows=rows, error_bad_lines=False, )
    # replaces "nan" from panda with empty string
    df = df.fillna('')

    for row_tuple in df.itertuples(index=False, name=None):
        for ind, itm in enumerate(map(detectInputType, row_tuple)):
            if itm[0] and not itm[1] == 0:
                # counts the occurrence for the type
                file_type_stats[ind][itm[0]] += 1
                if itm[1] >= file_type_length[ind]['data_length']:
                    file_type_length[ind]['data_length'] = itm[1]

    # COULD SEPARATE BELOW CODE TO A FUNCTION TO DISPLAY (AND GET MAX)
    # IF FILE STATS ALWAYS RUN FOR THE ENTIRE CONTENT OF FILE
    for ind, col in enumerate(file_type_stats, 1):
        # prints the occurrence % of all the types in the column greatest to least
        ascending = sorted(col.items(), key=lambda x: x[1], reverse=True)
        max_list.append(ascending[0][0])

        # display only when required.  recommendation does't need it displayed
        if display:
            print("column {}: {}".format(ind, file_headers[ind - 1] if file_headers else "no name"))
            print("\n".join(
                "entry type {:<4} - {:.1f}%".format(key, value * 100 / (sum(col.values()) or 1)) for key, value in
                ascending))
            print()

    for index, val in enumerate(max_list):
        if val == "VARCHAR()":
            max_list[index] = "VARCHAR({})".format(file_type_length[index]['data_length'] * 2)

    return max_list


# DISPLAYS MAX TYPE OF COLUMN DATA TYPES
# STILL UNDECIDED IF IT IS A BETTER IMPLEMENTATION
def displayColumnStats():
    pass


# RETRIEVES ALL TABLES FROM CONNECTED DATABASE.  OPTIONAL.
def getTableList():
    # CHECKS TO SEE WHAT TABLES ARE AVAILABLE
    # ORACLE:
    # sql = 'SELECT owner, table_name FROM all_tables
    # SQLLITE:
    sql = 'SELECT name FROM sqlite_master WHERE type = "table" ORDER BY name'
    cursor.execute(sql)

    rows = cursor.fetchall()

    print("These are the available tables:  ")
    for row in rows:
        print(row[0])


def convert(val, key):
    val = str(val)

    # Ensures single quotes are escaped.  
    val = val.replace("'", "''")

    # ORACLE:
    # if key.upper() == 'DATE' or key.upper() == 'TIMESTAMP':
    #     date = str(parser.parse(val))  # will raise ValueError if bad date
    #     return "TO_TIMESTAMP('{}', '{}')".format(date, argDict['dateFormat'])
    #
    # if key.upper() == 'NUMBER':
    #     number = str(float(val))
    #     return "TO_NUMBER('{}')".format(number)

    # FOR TESTING PURPOSES ONLY:  catching date parser errors, but not activating the conversion
    # SQLLITE = NB: NO DATE CONVERSION IN SQLLITE
    if key.upper() == 'TEXT' and val:
        # raiseS ValueError if bad date
        date = str(parser.parse(val))
        return "'TO_TIMESTAMP({}, {})'".format(date, argDict['dateFormat'])

    if key.upper() == 'INTEGER' or key.upper() == 'REAL':
        if not val == '' or val is not None:
            try:
                number = str(float(val))
                # converts it to a float
                # return "CAST('{}' as decimal)".format(number)
            except ValueError:
                pass

    # otherwise return value with quotes, making it a string
    # TO DO:  oracle may raise error if string not convertible to its specified data type
    return "'{}'".format(val)


# MAY USE IN THE FUTURE
def errorMessage(line, msg=""):
    print("fatal error: bad data on line {}".format(str(line)))
    print(msg, "\n")


def about():
    print("TBD: ABOUT")


if __name__ == '__main__':
    main()
