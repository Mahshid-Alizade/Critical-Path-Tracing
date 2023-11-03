# Description
The implementation of critical path tracing algorithm on combinational circuits, using Java programming language.

## Critical path tracing
For a fanout-free circuit, </br>
critical path tracing is a simple tree traversal procedure that marks as critical, </br>
and recursively follows every sensitive input of a gate in turn with critical output.

## Input
- A piece of hardware description code ( VHD or Bench )
- A set of test vectors which is randomly generated
  
## Output
- The set of detectable "stuck-at" problems in the input circuit for the given vectors

## Result
The proposed code was tested on C499.bench and C432.bench,
the output is as it is shown below:

### bench 499
<img width="650" alt="image" src="https://github.com/Mahshid-Alizade/Critical-Path-tracing/assets/42897108/32b887c1-12f9-4adb-be29-1e8f1557f890">

### bench 432
<img width="634" alt="image" src="https://github.com/Mahshid-Alizade/Critical-Path-tracing/assets/42897108/b9f219ef-f966-43e2-a715-90fe8dfdd23b">
