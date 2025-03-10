## Scala Space Shooter Game

### Description
- simple space shooter game developed using Scala and ScalaFX. 

### Features
- Player-controlled spaceship
- Variety of spaceships to choose from
- Enemy ships with increasing difficulty
- Score tracking and leaderboard
- Dynamic user interface with custom visual effects, animations, and sound effects

### Dependencies
- ScalaFX: `org.scalafx" %% "scalafx" % "8.0.192-R14`
- ScalaFXML: `org.scalafx" %% "scalafxml-core-sfx8" % "0.5`
- Macro Paradise: `org.scalamacros % paradise % 2.1.1` (compiler plugin)

### MVC Architecture
- [**Model**](src/main/scala/com/example/model): `Spaceship`, `Laser`, `Enemy`, `Leaderboard`, `LeaderboardEntry`
- [**View**](src/main/resources/com/example/view) & [**Controller**](src/main/scala/com/example/controller): `GameEnd`, `Game`, `Instruction`, `Leaderboard`, `MainMenu`, `SpaceshipSelection`, `UserInput`

### Resources
- [**Images**](src/main/resources/images): `background`, `effect`, `enemy`, `icons`, `spaceship`

### YouTube Demo
- [Scala Space Shooting Game](https://youtu.be/gxv_pGzMu0c)

### Project Structure
```
├───src
│   └───main
│       ├───resources
│       │   ├───com
│       │   │   └───example
│       │   │       └───view
│       │   ├───images
│       │   │   ├───background
│       │   │   ├───effect
│       │   │   ├───enemy
│       │   │   ├───icons
│       │   │   └───spaceship
│       │   ├───sounds
│       │   └───style
│       └───scala
│           └───com
│               └───example
│                   ├───controller
│                   ├───model
│                   └───util
```
