# tcp
TCP client-server pair


Notes:

As soon as we have to use "standard serialization" the custom services must have Serializable args and return types of all methods
And so the Optional<T> can not be used to manage return value :(

Roadmap:

- push the draft
- configure logging
- iterate over requirements





done:
- use serializable object for data transition
- read server port from command line argument (get rid of owner lib)
- make TaskRes class for result object (including void calls support)
- add ID to the task/result objects
- add TaskRes errors support for responding the errors 
- add several named services support at server side
- implement single socket per client approach
  - at client side (shared client)

planned:
- implement single socket per client approach
  - at server side
- split Client.Main into number of tests
