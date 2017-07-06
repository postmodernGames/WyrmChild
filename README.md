# WyrmChild
An Earley parser project


State needs a toString
  This should have realized a s a consequence of a general rule about the representability of objects
  
 Testing
 
 For every state,
   If the marker follows a nonterminal, it was added by completion
   If it follows nothing, it was added by prediction
   If it follows a terminal, it was added by scanning
   
  If it was predicted, with LHS Y, there must be a state with a marker before Y in the same set.
  If it was completed, with LHS Y and marker before X, there must be a completed state with LHS X and input position j in the same set and a state with the marker before X in S[j]
  The first state every every set (except S[0]) should be scanned in, and there are no other scanned-ins
   
   
