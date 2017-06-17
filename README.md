# tcp
TCP client-server pair

Roadmap:

- push the draft
- configure logging
- iterate over requirements





done:
- use serializable object for data transition

planned:
- read server port from command line argument (get rid of owner lib)
- make TaskRes class for result object (including void calls support)
- add UUI to the task/result objects
- add TaskRes errors support for responding the errors 
- add several named services support at server side
- implement single socket per client approach
  - at server side
  - at client side (shared client)

