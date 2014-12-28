# JniBox

## Overview

JniBox library intends to manage the dynamic loading of libraries when the native library is
packed as a resource in the jar or another package type (like nar packaging). So dealing with JNI
and dynamic libraries there are two strategies:

* "I'm going to put the native library that implements the JNI header in some well known path
that all the targeted devices are going to share, so I can refer that path in the java code with
no problems"

* "I'm going to put the native library that implements the JNI header within the jar, or packed
in a nar"

There are some issues that you have to manage additionally like os, arch and linker portability.
But there are nice maven plugins like nar-maven-plugin to solve this stuff.

Let's focus in the second point, that is the actually portable, the one that does not require to
mess up the device environment depending on static resource paths that probably implies future
problems (suppose someone removes that library or you have to update that library that is out
of the jar). And here JniBox starts, you have your jar with all the native libraries within packed
and you want to create those instances of JNI classes loading previously the native libraries.
How to do this keeping the code and design clear?

### Overview by example

Suppose you have a JNI class named `org.mycompany.myproject.ComplexMath` and the native library
with the JNI headers, implementations and whatever compiled in a file as a resource named 
`org/mycompany/myproject/native/complex_math.so`.

Two steps:

* Load native library. For that point you have to know the package of the resource
(`org.mycompany.myproject.native`) and the name of the resource (`complexMath.os`).

* Create the instance of your JNI class.

JniBox offers support to manage the first point.

## What is JniBox for?

We want to keep the environment clean and clear. JniBox helps you to:
 
* Unpack the library and place it somewhere safe,

* Load the library when required,

* Unload the library and remove it when the application is closed,

* Avoid conflicts of versions when load the library, so keep the library locally, and

* Spring way.