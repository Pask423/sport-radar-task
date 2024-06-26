# ScoreBoard

## Overview

`ScoreBoard` is a Java-based library for tracking football games.

## Features

- Start a new game.
- Update the score of an ongoing game.
- Finish a game and retrieve its state.
- Get a summary of all ongoing games, sorted by a predefined comparator.

## Installation

### Prerequisites

- Java 21 or higher
- Maven or Gradle for dependency management

### Using Maven

Add the following dependency to your `pom.xml` file:

```xml
<dependency>
    <groupId>com.ps</groupId>
    <artifactId>sport-radar-task</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Using Gradle
Add the following dependency to your build.gradle file:
```groovy
implementation 'com.ps:sport-radar-task:1.0.0'
```

## Usage

### Initial Setup
Create an instance of DefaultScoreBoard with the necessary dependencies:

```java
import com.ps.store.GamesStore;
import com.ps.store.InMemoryGamesStore;
import com.ps.time.TimeProvider;
import com.ps.time.DefaultTimeProvider;
import com.ps.board.ScoreBoard;
import com.ps.board.DefaultScoreBoard;

import java.util.HashMap;

public class Main {
    public static void main(String[] args) {
        GamesStore store = new InMemoryGamesStore(new HashMap<>());
        TimeProvider timeProvider = new DefaultTimeProvider(); 

        ScoreBoard scoreBoard = new DefaultScoreBoard(store, timeProvider);
    }
}
```

### Starting a Game
```java
NewGame newGame = scoreBoard.startGame("HomeTeam", "AwayTeam");
System.out.println("New game started: " + newGame);
```

### Updating the Score
```java
UUID gameId = newGame.gameId();
Game updatedGame = scoreBoard.updateScore(gameId, 10, 5);
System.out.println("Updated game: " + updatedGame);
```

### Finishing a Game
```java
FinishedGame finishedGame = scoreBoard.finishGame(gameId);
System.out.println("Finished game: " + finishedGame);
```

### Getting the Summary
```java
ScoreBoardSummary summary = scoreBoard.getSummary();
summary.getGames().forEach(System.out::println);
```

## Design Decisions

### Exception model
Currently, it is a common consensus that managing the flow of application via the use of exceptions is not a good practise.

We should use some other way to indicate success or failure, like Either or more complex return types.

However, as the code is supposed to be as simple as possible I decide to go with expedition on last layer.

Firstly, it is by far the simples appcache to indicating failure. 
I could have used some rich model for return types, but I decided that it would be too complex as for the spirit of this task.

Secondly, I didn't want to introduce dependency to vavr, to get Either, as it will add vavr as transitive dependency fot the users.
Again, I could have written my own Either like class, but it will force the users to relay on this handling model, which is still not a common practise in Java. 

### Concurrency support
Despite a goal in mind to keep the implementation simple 
I decide that correct way of handling concurrent access is a reasonable edge-case to handle.
My concurrency handling is based on ReentrantLock providing locking withing single Game.

### Lack of concurrent multi operation test
Writing a test that will call few methods in concurrent fashion with zero or minimal delay is very hard.
As in current setting all the methods need to be executed almost at once, what leave a lot of space of randomization.
If order of method execution inside the test will change the test will fail.
In such situation it is very easy for test to become flaky.

### Smaller than current scores
Theoretically, while uncommon there may be a case when a goal was canceled after it was added.
I dedicated to allow the users for updating the scores with value less the current score.

### Equal scores
In case when user tries to update the score with the same value for both teams, there will be no real update, as to spare potentially heavy datastore operations.
The client will get a current state of a game without throwing an exception or performing an update.

