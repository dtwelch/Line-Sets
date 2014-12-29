An Implementation of LineSets in Processing/Java
=========

This small project is an implementation of a new-ish set based visualization technique known as LineSets. For more 
information on LineSets and the flavor of visualization contained in this project, see 

    http://research.microsoft.com/en-us/um/people/nath/docs/linesets_infovis2011.pdf
    
Installation Information
=========
Users will need to first download and install Processing and have maven installed. Unfortunately however, even
though I bothered 'mavenizing' the project, users will still need to manually download the following jars to their
own local maven repos (as they don't exist in the central repo):

- The core processing library: There are a number of tutorials that describe how to make processing's libraries 
visible to your favorite IDE:

    - eclipse    https://processing.org/tutorials/eclipse/
    - intelliJ http://www.slideshare.net/eskimoblood/processing-in-intellij or 
    http://www.bensimms.co.uk/using-intellij-with-processing-and-why/
    
- Unfolding maps: http://unfoldingmaps.org/

Once the following jars have been downloaded, the following tutorial describes nicely (probably better than I could) 
the commands needed to add these jars to your local maven repo.

Thanks for reading.
