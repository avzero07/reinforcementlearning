# Reinforcement Learning
The goal of the project is to build a learning agent using principles of reinforcement learning. The learning agent will, in this case, be a custom robot tank which learn to fight in RoboCode.

# Language : Java

# Project Phases

The overall project will be divided into 3 phases.

## Phase 1
### The Error Backpropagation Algorithm
The goal of Phase 1 is to develop methods that can be used to deploy a 3 layer Artifical Neural Network (ANN) that can learn any n-input and n-output problem using the Error Backpropagation Algorithm.
#### Status : Complete
#### Milestones
- Understand the Error Backpropagation Algorithm
- Create a high level design for the implementation
    - CommonInterface      : Specfies baseline methods
    - NeuralNet Interface  : Extends the CommonInterface and specifies baseline methods to be used in all NeuralNets
    - NeuralNet Class      : Implements the NeuralNet Interface 
- Develop unit tests
    - Used to test all functions in the NeuralNet Class
- Develop methods
- Use methods to build a 3 layer ANN and test it on the XOR Problem

## Phase 2
The goal of Phase 2 is to develop methods that can be used to implement the Temporal Difference Algorithm (TD) using Look Up Tables (LUT). This will then be used to train a custom robot tank in RoboCode.
### Reinforcement Learning : The Temporal Difference Algorithm
#### Status : Complete
#### Milestones
- Understand the Q Learning algorithm
- Create a high level design for the implementation
    - LUT Interface        : Extends the CommonInterface and specifies baseline methods to be used in all LUTs
    - LUT Class            : Implementes the LUT Interface
- Develop methods to implement the Temporal Difference algorithm
- Build a cusom Robot Tank in Robocode
    - Use methods to implement Q Learning and train the custom robot against an enemy tank
    - Best win rate 65%

## Phase 3
The goal of Phase 3 is to make use of the results of Phase 1 and Phase 2 in an effort to make the custom Robot "intelligent". That is, incorporate Error Backpropagation in the TD algorithm.
### Putting it all together
#### Status : Complete
#### Milestones
- Replace the LUT from Phase 2 with a Multilayer NN using modules developed in Phase 1
    - Use LUT from best case in Phase 2 to train NN and determine architecture
- Overload Q Learning methods to make use of NN
- Train Robot tank against Sample.Fire.
    - Win rate upto 90%

## Phase 4
Code cleanup and housekeeping.
#### Status : Pending
