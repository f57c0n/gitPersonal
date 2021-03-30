# SQL LOADER

The program provides different menu options for the user to either accept the application's recommended table data types
or to enter them manually.
<br><br>
The program reads the entire input file and removes any rows with invalid entries.  These invalid entries are stored in an output file to be validated and re-entered at a later time.
<br><br>
The user can view the cleansed file for review before committing the migration.


## Program Flow

TO BE ADDED!


## Main Menu Options
Table Menu Option:
1.  Overwrite Existing Table 
2.  Append to Existing Table 
3.  Create and Insert to New Table 
4.  Drop Existing Table 
5.  List Existing Table 
6.  Get File Column Statistics
7.  S: Start Another Migration
8.  Q: Exit Application

## Column Menu Options
Options To Determine Columns:
1.  Use BOTH detected column names and types 
2.  Use detected column name but enter column type 
3.  Enter column name but used detected column type 
4.  Enter BOTH column names and types
5.  Get column type statistics
6.  0:  Exit Without Inserting Data
7.  Q:  Exit Application