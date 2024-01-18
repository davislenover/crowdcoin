![CrowdCoin x Ratchet](crowdcoin/imgs/CrowdCoinImg.jpg)

# CrowdCoin
CrowdCoin enables a group of users to both keep track of item valuations all in one place. The backend utilizes an SQL database while the frontend employs Ratchet, a framework on-top of JavaFX, designed to streamline the process of modifying both UI elements and their behaviors. All-together this allows users to interact with the database in an intuitive manner while ensuring the system is scalable and maintainable.

# Status of Project
- Both CrowdCoin and Ratchet are still in active development! This guide will evolve overtime as development continues.
- Currently, CrowdCoin is operable but requires more technical know-how to get up and running. Below is the status of each feature being implemented

# Installation
## Preview Build
### Dependencies
- OpenJDK 20
- Java 8 or later
- You can use an IDE of choice (such as Intelij which will handle a lot of maven's jdk and javafx dependencies for you) or the Terminal
- It's assumed Maven is correctly setup and operating with OpenJDK 20
### Instructions (All Platforms)
1. Clone the repository to your local machine ```git clone https://github.com/davislenover/crowdcoin.git```
2. Navigate to the ```crowdcoinproject``` directory (```crowdcoin/crowdcoinproject```)
3. To build a preview jar with Ratchet dependencies, run the following: ```mvn clean install```
4. Navigate to ```crowdcoin/crowdcoin/target``` and execute the jar with dependencies

### Definition of Done

-- Item operates as expected by client and is functionally sound (i.e. has been proven correct via testing by programmer) --

### Product Backlog
Status: Pending (P), Started (S), Blocked (B), Done (D)

| Id  | TODO title                                          | Who?               | Start      | End        | Status                                                              |
|:---:|-----------------------------------------------------|--------------------|------------|------------|---------------------------------------------------------------------|
| F01 | Refactor to improve readability and maintainability | Davis Lenover      | 2023-04-30 | 2023-05-01 | D                                                                   |
| F02 | Read and create table from SQL database             | Davis Lenover      | 2023-05-01 | 2023-05-03 | D                                                                   |
| F03 | Tabs define how sidebar logic works                 | Davis Lenover      | 2023-05-03 | 2023-05-06 | D                                                                   |
| F04 | Menu bar is defined by groups of buttons            | Davis Lenover      | 2023-05-08 | 2023-05-10 | D                                                                   |
| F05 | Previous and Forward functionality for TableView    | Davis Lenover      | 2023-05-16 | 2023-05-20 | D                                                                   |
| F06 | Additional User Input Objects for sidebar grid      | Davis Lenover      | 2023-05-22 | NA         | B (Will always be adding input objects as seen fit)                 |
| F07 | Write to table from SQL database                    | Davis Lenover      | 2023-06-03 | 2023-06-07 | B (This is technically complete but may require more functionality) |
| F08 | Pop-up Windows                                      | Davis Lenover      | 2023-06-05 | 2023-06-10 | D                                                                   |
| F09 | Filters for Tabs                                    | Davis Lenover      | 2023-06-10 | 2023-07-03 | D                                                                   |
| F10 | Input Checking for Fields                           | Davis Lenover      | 2023-06-27 | NA         | B (Will always be adding validators as seen fit)                    |
| F11 | New Entry for Tabs                                  | Davis Lenover      | 2023-07-03 | 2023-07-10 | D                                                                   |
| F12 | Edit/Delete Entries in main Tab area                | Davis Lenover      | 2023-07-10 | 2023-07-15 | D                                                                   |
| F13 | Export Tabs to csv                                  | Davis Lenover      | 2023-07-15 | 2023-07-18 | D                                                                   |
| F14 | Work Tabs                                           | Davis Lenover      | 2023-07-18 |  | B (Implenting Adding/Removing Users to test F14)                    |
| F15 | Add/Remove Users (Admin)                            | Davis Lenover      | 2023-08-04 |  | S (Working in tandem with F16)                                      |
| F16 | Exception/Query Exception System                    | Davis Lenover      | 2023-08-08 |  | S                                                                   |
| F17 | Multithreading System                               | Davis Lenover      | 2023-08-22 |  | S                                                                   |
| F18 | Move Ratchet java files to dedicated Ratchet module | Davis Lenover      | 2023-09-09 | 2023-09-11 | D (Will be moving more files as seen fit)                                                                   |
| F19 | SQLTable (and subsequent modules) rework | Davis Lenover      | 2023-12-22 |  | S                                                                  |
