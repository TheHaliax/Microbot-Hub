# Pyramid Plunder Plugin

Automates the Pyramid Plunder minigame with full overlay support, intelligent loot logic, and real-time timer management. This plugin provides comprehensive automation for one of RuneScape's most popular thieving training methods.

## Features

- **Complete Minigame Automation**: Automatically navigates through all 8 rooms of the pyramid
- **Smart Loot Management**: Configurable logic for urns, chests, and sarcophagi based on your thieving level
- **Real-time Timer**: Custom infobox timer with low-time warnings and color coding
- **Visual Overlays**: Highlights important objects like tomb doors, spear traps, and loot containers
- **Progress Tracking**: Comprehensive statistics including XP gains, items looted, and runs completed
- **Safety Features**: Automatic prayer management and health monitoring
- **Configurable Behavior**: Extensive settings for customizing your looting strategy

## Requirements

### Prerequisites
- **Thieving Level**: Minimum level 21 (increases for higher rooms)
- **Location Access**: Must be able to reach the Pyramid Plunder minigame in Sophanem
- **Supplies**: Antipoison potions and prayer potions
- **Equipment**: Recommended to have good defensive gear

### Thieving Level Requirements
- **Room 1**: 21 Thieving
- **Room 2**: 31 Thieving  
- **Room 3**: 41 Thieving
- **Room 4**: 51 Thieving
- **Room 5**: 61 Thieving
- **Room 6**: 71 Thieving
- **Room 7**: 81 Thieving
- **Room 8**: 91 Thieving

## How It Works

The plugin operates in a systematic progression through the pyramid:

1. **Initialization**: Checks supplies and prepares for the run
2. **Pyramid Entry**: Handles mummy encounters and enters the pyramid
3. **Room Progression**: Automatically loots each room based on your configuration
4. **Loot Strategy**: Uses intelligent logic to determine which containers to loot
5. **Exit & Banking**: Safely exits and banks valuable artifacts
6. **Repeat**: Continues the cycle for optimal XP gains

## Configuration Options

### Loot Settings
- **Keep Valuable Artefacts**: Automatically keeps high-value items
- **Keep Ivory Comb**: Option to retain or drop ivory comb artifacts
- **Urn Logic**: Choose between highest floor, highest 2 floors, or timer-based looting
- **Chest Logic**: Configure chest looting strategy (all, highest 2, or highest 5 rooms)
- **Sarcophagus Logic**: Set sarcophagus looting preferences

### Overlay & Highlight Settings
- **Hide Default Timer**: Conceals the game's built-in timer
- **Show Exact Timer**: Displays precise time remaining as an infobox
- **Timer Low Warning**: Set when the timer changes color (default: 30 seconds)
- **Highlight Colors**: Customize colors for doors, spear traps, and containers
- **Visual Indicators**: Toggle highlighting for different object types

## Usage

1. **Prepare Supplies**: Ensure you have antipoison and prayer potions
2. **Set Configuration**: Adjust loot and overlay settings to your preference
3. **Start Plugin**: Enable from the Microbot plugin panel
4. **Monitor Progress**: Watch the overlay for real-time statistics
5. **Automatic Operation**: The plugin handles everything from entry to banking

## Overlay Information

The plugin provides two main overlays:

### **Main Overlay Panel**
- **Thieving XP**: Current level and XP gained this session
- **Strength XP**: Strength experience from mummy encounters
- **Urns Looted**: Count of successfully looted urns
- **Chests Looted**: Number of golden chests opened
- **Sarcophagi**: Count of sarcophagi looted
- **Runs Completed**: Total pyramid runs finished

### **Visual Highlights**
- **Tomb Doors**: Green highlights for accessible doors
- **Spear Traps**: Orange warnings for active traps
- **Loot Containers**: Yellow indicators for valuable objects

## Safety Features

- **Automatic Prayer Management**: Toggles Protect Melee and Rapid Heal as needed
- **Health Monitoring**: Ensures you don't get overwhelmed by mummies
- **Supply Checking**: Automatically banks when supplies are low
- **Smart Navigation**: Avoids dangerous areas and handles encounters safely

## Tips for Optimal Use

1. **Level Appropriately**: Ensure your thieving level matches your target rooms
2. **Supply Management**: Keep sufficient antipoison and prayer potions
3. **Configuration**: Adjust loot settings based on your goals (XP vs. profit)
4. **Monitor Progress**: Use the overlay to track your efficiency
5. **Bank Regularly**: Don't let your inventory fill up with low-value items

## Technical Details

- **Plugin Version**: 1.0.0
- **Author**: Hal
- **Minimum Client Version**: 1.9.6
- **Dependencies**: Core Microbot functionality only
- **Compatibility**: RuneLite with Microbot integration

## Support

For issues, questions, or feature requests, please refer to the main Microbot repository or contact the plugin author.

---

*This plugin provides comprehensive automation for the Pyramid Plunder minigame, making thieving training efficient and profitable while maintaining safety and customization options.*
