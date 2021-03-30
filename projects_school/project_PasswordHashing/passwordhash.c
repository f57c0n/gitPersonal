/*********************************
**	633 Assignment1							**
**	Password Hashing					  **
**	Submitted by:			          **
**		JJFalcon		              **
**********************************/

#include 	<stdio.h>
#include 	<stdlib.h>
#include 	<string.h>
#include	<ctype.h>

#define PASSLENGTH 13
#define MXUSERS 50
#define NAMELENGTH 33
#define MAXTryOut 3
#define FORM 100

char USERarray[MXUSERS][NAMELENGTH];
char PASSarray[MXUSERS][PASSLENGTH];
int NumofCurrentUsers = 0;
int TryOut[MXUSERS];

#define PASSLENGTH3 4
#define DICTIONARYLENGTH 456976

char PASSarray3[DICTIONARYLENGTH][PASSLENGTH3 + 1];
char HASHarray[DICTIONARYLENGTH][PASSLENGTH3 +1];


char * enterName()
{
	char * name = (char*)calloc(NAMELENGTH, sizeof(char));
	char format[FORM];
	printf("Enter your username, Type 0 to exit:\n");

	// limit name to max length
	sprintf(format, "%%%ds", NAMELENGTH-1);
	scanf(format, name);

	// get rid of all the extra characters
	while(getchar() != '\n')  {}

	// disallow NULL entry and instead exit the user off the program
	if (strcmp("0", name) == 0)
	{
		return (char*) -1;
	}

	// requires minimum length for username
	if (strlen(name) < 4)
	{
		printf("Username must be at least 4 characters.\n");
		free(name);
		return 0;
	}

	else
	{
		return name;
	}
}


char * enterPassword()
{
	char * pword = (char *)calloc(PASSLENGTH, sizeof(char));
	char format[FORM];
	printf("Enter Password: ");

	// limit password to max length
	sprintf(format, "%%%ds", PASSLENGTH-1);
	scanf(format, pword);

	// get rid of all the extra characters
	while (getchar() != '\n') { }

	// pads with zeros if password length is less than 12 characters
	int plength = strlen(pword);
	if (strlen(pword) < PASSLENGTH-1)
	{
		int i;
		for (i = plength; i < PASSLENGTH-1; i++)
		{
			strcat(pword,"0");
		}
	}

	return pword;
}

int ReadFromFile(char usernames[MXUSERS][NAMELENGTH], char pwords[MXUSERS][PASSLENGTH])
{
	FILE * file;
	int MXLINE = 120;
	char line[MXLINE];
	char newLine = '\n';

	if ((file = fopen("sample.txt", "rt")) == NULL)
	{
		printf("Error:  Can't open file.\n");
		return 0;
	}

	int ctr = 0;

	char * str = (char*)calloc(13, sizeof(char));

	// populate table
	while (fgets(line, MXLINE, file) != NULL)
	{
		sscanf(line, "%12s", str);

		// even = usernames, odd = passwords
		if (ctr % 2 == 0)
		{
			strncpy(usernames[ctr / 2], str, 12);
		}

		else
		{
			strncpy(pwords[ctr / 2], str, 12);
		}

		ctr++;
	}

	fclose(file);
	return ctr / 2;
}


void WriteToFile(char usernames[MXUSERS][NAMELENGTH], char pwords[MXUSERS][PASSLENGTH], int usrcount)
{
	FILE * output;
	char newLine = '\n';

	if ((output = fopen("sample.txt","w")) == NULL)
	{
		printf("Error:  Can't create file!\n");
		return;
	}

	usrcount--;

	while(usrcount > -1)
	{
		// write usernames
		if(fwrite((usernames[usrcount]), sizeof(char), (strlen(usernames[usrcount])), output) != strlen(usernames[usrcount]))
		{
			printf("Can't write usernames.\n");
			fclose(output);
			return;
		}

		// write newline after userame entry
		if(fwrite(&newLine, sizeof(char), 1, output) != 1)
		{
			printf("Can't write newlines\n");
			fclose(output);
			return;
		}

		// write hashed passwords
		if(fwrite((pwords[usrcount]), sizeof(char), (strlen(pwords[usrcount])), output) != strlen(pwords[usrcount]))
		{
			printf("Can't write hashed passwords\n");
			fclose(output);
			return;
		}

		// write newline after hashed password entry
		if(fwrite(&newLine, sizeof(char), 1, output) != 1)
		{
			printf("Can't write newlines\n");
			fclose(output);
			return;
		}

		usrcount--;
	}

	fclose(output);
	printf("File Written");

	return;

}

void E(char *in, char *out)
{
	// make the input characters uppercase.
	char c0 = toupper(in[0]);
	char c1 = toupper(in[1]);
	char c2 = toupper(in[2]);
	char c3 = toupper(in[3]);

	out[0] = (c0 & 0x80) ^ (((c0 >> 1) & 0x7F) ^ ((c0) & 0x7F));
	out[1] = ((c1 & 0x80) ^ ((c0 << 7) & 0x80)) ^ (((c1 >> 1) & 0x7F) ^ ((c1) & 0x7F));
	out[2] = ((c2 & 0x80) ^ ((c1 << 7) & 0x80)) ^ (((c2 >> 1) & 0x7F) ^ ((c2) & 0x7F));
	out[3] = ((c3 & 0x80) ^ ((c2 << 7) & 0x80)) ^ (((c3 >> 1) & 0x7F) ^ ((c3) & 0x7F));
}

char * Hashify(int n, char * pword)
{
	char * hash = (char*)calloc(PASSLENGTH, sizeof(char));

	if (hash == NULL)
	{
		printf("Error memory can't be allocated.\n");
		return hash;
	}

	int i;
	for (i = 0; i < n; i++)
	{
		E(&pword[4*i], &hash[4*i]);
	}

	return hash;
}

int checkUserNameTable(const char * username, char usernames[MXUSERS][NAMELENGTH])
{
	int i;
	char temp[NAMELENGTH];

	for (i = 0; i < MXUSERS; i++)
	{
		strncpy(temp, usernames[i], 12);

		int cmp = strcmp(username, temp);

		if (cmp == 0)
		{
			return i;
		}
	}

	return -1;
}

void enterNewUser(const char * username)
{
	printf("User not found\n");
	printf("Creating new user.\n");
	printf("Enter new user password:\n");

	char * pw = enterPassword();
	char * hash = Hashify(3, pw);

	strcpy(USERarray[NumofCurrentUsers], username);
	strcpy(PASSarray[NumofCurrentUsers], hash);

	NumofCurrentUsers++;
	printf("User added\n\n");
}

void enterExistingUserOLM(const char * username, int usernameIndex)
{
	int correct = 1;
	while ((TryOut[usernameIndex] < MAXTryOut) && (correct == 1))
	{
		char * pw = enterPassword();
		char * hash = Hashify(3, pw);

		int cmp = strcmp(hash, PASSarray[usernameIndex]);

		if (cmp == 0)
		{

			TryOut[usernameIndex] = 0;

			printf("User Authenticated.\n");
			correct = 0;
		}
		else
		{
		printf("Incorrect password, please try again:!\n");
		TryOut[usernameIndex]++;
		}
	}

	if (TryOut[usernameIndex] >= MAXTryOut)
	{
		printf("User Account Locked.\n\n");
	}
}

void xR(char* in, char* out, int r)
{
	// rehashing the hashed password
	out[3] = in[3] ^ (r & 255);
	r = r >> 8;
	out[2] = in[2] ^ (r & 255);
	r = r >> 8;
	out[1] = in[1] ^ (r & 255);
	r = r >> 8;
	out[0] = in[0] ^ (r & 255);
}

char * generatexR(char* pass, int r)
{
	// generate xOR for hash with server's random #
	char * out = (char*)calloc(PASSLENGTH, sizeof(char));
	xR(pass, out, r);
	xR(&pass[4], &out[4], r);
	xR(&pass[8], &out[8], r);
	return out;
}

void enterExistingUserCRA(const char * username, int usernameIndex)
{
		char * pw = enterPassword();
		char * hash = Hashify(3, pw);

		int r = rand();

		// generate xOr on new hash on client and server side
		char* clientOut = generatexR(hash, r);
		char* serverOut = generatexR(PASSarray[usernameIndex], r);

		int cmp = strcmp(clientOut, serverOut);

		if (cmp == 0)
		{
			printf("access granted\n");
		}
		else
		{
			printf("access denied\n");
		}
}

int authenticateThis(int choice)
{
	NumofCurrentUsers = ReadFromFile(USERarray, PASSarray);

	while (1){
		// check for initial input (a username), when a valid input is given, the program proceeds.
		char * namePTR = 0;
		while (namePTR == 0)
		{

			namePTR = enterName();

			// write to file upon exit
			if (namePTR == (char*) -1)
			{
				WriteToFile(USERarray, PASSarray, NumofCurrentUsers);
				return 0;
			}
		}

		// check if username exist and retrieve index from the table.
		int usernameIndex = checkUserNameTable(namePTR, USERarray);
		// username not found.
		if (usernameIndex == -1)
		{
			enterNewUser(namePTR);
		}
		// username found.
		else
		{
			if (choice == 1)
			{
				enterExistingUserOLM(namePTR, usernameIndex);
				return 0;
			}
			else
			{
				enterExistingUserCRA(namePTR, usernameIndex);
				return 0;
			}
		}
	}
	return 0;
}

int position = 0;
void populateDictionary(char * pass, int index, int max)
{

	// 4 letter words
	if (index >= max)
	{
		// hash the password
		char * hash = Hashify(1, pass);
		//printf("%s : %s\n", pass, hash);

		// store password and hash into arrays
		strncpy(PASSarray3[position], pass, 4);
		strncpy(HASHarray[position], hash, 4);

		printf("position %d = %s hashed to: %s\n", position,PASSarray3[position], HASHarray[position]);

		position++;
	}
	else
	{
		// cycle through from A - Z until 4 letter word
		for (int i = 0; i < 26; i++)
		{
			char ch = 'A' + i;
			pass[index] = ch;
			populateDictionary(pass, index + 1, max);
		}
	}
}

int crackIt()
{
	char * initpass = (char*)calloc(PASSLENGTH3 + 1, sizeof(char));
	// set last character to NULL
	initpass[PASSLENGTH3] = 0;
	// populate Dictionary
	populateDictionary(initpass, 0, PASSLENGTH3);

	while (1)
	{
		//allocate a string of the max username length
		char * hash = (char*)calloc(12, sizeof(char));
		printf("Enter 4-character password hash. Type 0 to exit:\n");
		scanf("%10s", hash);

		// get rid of all the extra characters
		char c;
		do { c = getchar(); } while (c != '\n' && c != '\0');
		fflush(stdin);

		// exit program
		if (strcmp("0", hash) == 0){
			return -1;
		}
		// hash entry must 4 character length
		if (strlen(hash) != 4)
		{
			printf("Invalid.  Enter 4-character hashed password. Please try again.\n");
			free(hash);
			continue;
		}

		int i;
		int found = 0;
		// search table for matching hash and output unhased password
		for (i = 0; i < DICTIONARYLENGTH; i++)
		{
			if (strcmp(HASHarray[i], hash) == 0)
			{
				printf("Found [%s] and it translates to: [%s]\n", hash, PASSarray3[i]);
				found = 1;
				break;
			}
		}

		if (found == 0)
		{
			printf("[%s] Not Found.\n", hash);
		}
	}
	return 0;
}

int main()
{
	char option[60] = "Choose a program to run from below options:";

	do
	{
		// display options for user to choose
		printf("\n%s\n", option);
		printf("Enter '1' : OLM Hash\n");
		printf("Enter '2' : Client Response Authorization\n");
		printf("Enter '3' : O Password Cracker\n");
		printf("Enter '0' : Exit.\n");
		int input = -1;
		scanf("%d", &input);
		switch (input) {
			case 0 :
			return 0;

			case 1:
			printf("Performing:  OLM Hash\n");
			authenticateThis(1);
			strncpy(option, "Choose another program to run from below options:",60);
			break;

			case 2:
			printf("Performing: Client Response Authorization\n");
			authenticateThis(2);
			strncpy(option, "Choose program to run from below options:",60);
			break;

			case 3:
			printf("Performing: O Password Cracker\n");
			crackIt();
			strncpy(option, "Choose another program to run from the following options:",60);

			break;

			default :
			printf("Invalid input. Please try again.\n");
		}
	} while ( 1);
	return 0;

}
