# QVC Survivors

A Vampire Survivors-style bullet-heaven roguelite set in the QVC/HSN retail universe. Survive waves of deal-hungry shoppers with an ever-growing arsenal of home-shopping weapons.

**Internal fun project — not an official QVC product.**

## Features

- **Weapons:** Flash Sale Strike, Spilled Coffee Zone, Clearance Cannon, On-Air Blaster, and more — all with 8-level upgrade paths and evolution chains
- **Passives:** Shipping Speed, QVC Q Credit, Fulfillment Center Efficiency, and others that amplify your build
- **Enemies:** Bargain Hunters, Deal Stampede, Return Raiders, and boss-tier Showhost Specials
- **Zones:** Studio Floor, Warehouse, Call Center, and more arena layouts
- **Meta-progression:** Unlock permanent upgrades between runs via Q-Points
- **Level-up screen:** VS-style card selection with reroll, skip, and banish

## Controls

| Action | Keyboard | Controller |
|--------|----------|------------|
| Move | WASD / Arrow keys | Left stick |
| Aim (manual) | Mouse | Right stick |
| Pause | Esc | Start |

Movement is all you need — weapons fire automatically.

## How to Run

**Download a release:**
1. Download the latest `QVCSurvivors-*.jar` from Releases
2. Run: `java -cp QVCSurvivors-3.0.0.jar com.qvc.survivors.Launcher`
   — or use the provided `launch.sh` (macOS/Linux) / `launch.bat` (Windows)

**Requirements:** Java 17+

## Build from Source

```bash
git clone <repo-url>
cd qvc-survivors
mvn package -Pcross-platform
java -cp target/QVCSurvivors-3.0.0.jar com.qvc.survivors.Launcher
```

The `cross-platform` profile bundles JavaFX so no separate FX install is needed.

## Tech Stack

- Java 17 + JavaFX 21 (rendering, input, audio)
- Maven (build, shade plugin for fat JAR)
- Lombok (boilerplate reduction)
- SLF4J + Logback (logging)
- JUnit 5 + Mockito (tests)

## Credits

Built for fun by the QVC tech team. Inspired by Vampire Survivors (poncle).
QVC, HSN, and Qurate are trademarks of Qurate Retail Group.
