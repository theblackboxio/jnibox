# jnibox-core

## Overview

There are two entities in `jnibox-core`:

* `JniRepository` that represents a repository of JNI libraries, and

* `JniLibrary` that represents a JNI library.

## JniRepository

A `JniRepository` is an object that manages the store and loading of JNI libraries in runtime. It 
has a repository directory (`repositoryDirectory`), that is where the native libraries are going to be stored. Any library
loaded in the repository is asked to habe a `package` and a `name`. The package is a java-like
package. Suppose that we load a native library with package name `org.gunner.bullet` and name
 `cal50`, then the repository is going to store it as:
 
    repositoryDirectory
                     |- org
                         |- gunner
                                |- bullet
                                       |- cal50
                                      
Is the same pattern of java package in classes.

When the repository is asked to load a library then the repository stores under the repository 
directory and loads it. When the repository is closed the repository directory and all its children
are deleted.

There are two implementations of `JniRepository` depending on the repository Directory strategy.
One is `ConfigurableJniRepository` that delegates in the user the choose of the directory.
Another is `TempDirJniRepository` that uses the property `java.io.tmpdir` to create a directory
that is probably unique for the instance of the repository. See documentation of `TmpDirJniRepository`.

