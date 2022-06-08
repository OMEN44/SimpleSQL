# SimpleSQL
SQL library for using MySQL and SQLite. Use objects to improve usability instead of strings and SQL statements.
# How to use this Library:
##Components of this library:
####Connectors:
To create a database connection you need a database template for example a `MySQL` object is a database 
template for a mySQL database, this holds the database credentials. The connector object is used to store the 
connection to the database, it can be used to get the db template, connection time, connection status and can be 
used to execute and queries and updates.<br>
####Entities:
 - A table object is designed to hold a list or rows and a list of columns as well as the tables name, it can be gotten in 
   two ways: <br>
   - The `createTable` class, this produces a table that can be edited and written to the database, the purpose of this
     is for creating new tables and editing the structure of the database. It can contain data, but its main intention is for 
     setting up databases.<br>
   - The `TableByName` class, this uses the name of the table and the connector in used to get a table object. This table 
     objects purpose is for reading and getting data. It cannot be edited or written to a database to prevent data 
     duplication and errors.
 - Columns and rows are similar in the way that they are both containers for cells, the 
   difference being that columns have a name and hold constraints and the column name and constraints is held by the 
   cells in a row.
 - Cells can contain unique and primary constraints, but it is more likely that these constraints will be accessible from 
   its column which will be held by the cell given it's not a BasicCell


### How to create a connection:
### How to execute simple queries and updates:
<p>It is assumed that the connection above has been created</p>
