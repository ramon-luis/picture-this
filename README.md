# Picture This  

Pictures This is an image editing application that can be used to apply various visual effects and edits to user photos.  The program uses lambda expressions to take advantage of the functional programming capabilities of Java8.  

![Picture This Screenshot 1](https://github.com/ramon-luis/picture-this/tree/master/demo/picture-this-screenshot-1.png "Picture This Screenshot 1")

## What's Here  
  * `Command Center.java` - manages the 'memory' of the application: original image, recent changes, recent files, etc.  
  * `Controller.java` - primary logic for the application  
  * `Main.java` - program entry (Main method)  
  * `MaxSizeStack.java` - stack data structure that maintains a maximum size (used for undo/redo)  

## Getting Started / Installing / Deployment  

Compile the src directory and call Main.  The main method is located in:  
`src/main/java/seatGeekPortal/SeatGeekMain.java`  

```
$ javac src/main/java/seatGeekPortal/SeatGeekMain.java
$ java src/main/java/seatGeekPortal/SeatGeekMain
```

Or open in your favorite IDE (Eclipse, IntelliJ) and run Main in SeatGeekMain.java  

### Prerequisites  

SeatGeek API Key [free](http://platform.seatgeek.com/) - to access data  

## Built With  

* [Java](http://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html)  
* [Maven](https://maven.apache.org/) - Dependency Management  

## Author

* [**Ramon-Luis**](https://github.com/ramon-luis)  

## Acknowledgments

* Thank you to Adam Gerber at the University of Chicago