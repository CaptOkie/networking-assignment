# networking-assignment

## Setup
Development done in Java 8.

### Step 1 - Obtain Source

```bash
$ git clone git@github.com:CaptOkie/networking-assignment.git
```

### Step 2 - Import Projects (For Eclipse)

1. File > New > Other...
2. Java Project > Next
3. Uncheck *Use default location*
4. Select *Browse...*
5. Navigate to where the repository was cloned
6. Select *networking-assignment/common/*
7. OK > Next > Finish
8. Repeat 1 - 7 for *networking-assignment/client* and *networking-assignment/server*

### Step 3 - Update Build Paths

1. *Right Click* on the *client* project
2. Select *Properties*
3. Java Build Path > Projects > Add...
4. Select *common*
5. OK > OK
6. Repeat 1 - 5 for the *server* project
