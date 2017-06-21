# tcp
TCP client-server pair


Notes:

As soon as we have to use "standard serialization" the custom services must have Serializable args and return types of all methods
And so the Optional<T> can not be used to manage return value :(

Locking whole socket instead of just out-stream is not safe (in long term) but looks more readable.

Do we use thread-safe logging infra?




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
  - at server side
- parallel message processing, concurrent responding to a client

planned:

- client must not listen the socket as soon as it get all responses
  and have to start listening each time it get a req
- split Client.Main into number of tests
- write a test that uses a clietn concurrently

