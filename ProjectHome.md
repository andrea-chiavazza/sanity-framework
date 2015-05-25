Some of the more interesting tools:
  * An “Ob” abstract class that provides a consistent implementations of equals(), hashCode() and toString() to its sub-classes. It works by recursively following its final fields using reflections.
  * 2 utilities functions XMLRead and XMLWrite to load and save immutable objects to XML.
  * A persistence library for immutable objects. I made it out of frustration due to most persistence libraries expecting objects to be mutable. The library ended up being slightly big and complex and might still have some design flaws, but it mostly works and is compatible with MySQL, Postgresql and HyperSQL. It expects the user to use the pcollections library rather than the collections from the standard library.
  * A Java implementation of the Clojure map/pmap functions meant as a safe way to write concurrent code. The functions are contained in a class called MapFunc.
  * A GUI tool able to view and edit composite data using reflection. The class responsible is ValueEditor.
  * 2 GUI tools GuiValueGroup and GuiValueGroupEditor able to view and edit immutable data without relying on reflection.

The scope of these utilities is very broad.<br />
This project might be re-organized in a future.