def assetType;

// ######################### //
// Create Person Asset Types //
// ######################### //

assetType = assetBuilder.newAssetType 'employee', 'SiteWhere Employee'
assetType.asPerson() withImageUrl 'https://s3.amazonaws.com/sitewhere-demo/sitewhere-symbol.png'
assetType.withDescription 'An employee of SiteWhere'
assetBuilder.persist assetType

// ######################### //
// Create Device Asset Types //
// ######################### //

assetType = assetBuilder.newAssetType 'galaxytab3', 'Galaxy Tab 3'
assetType.asDevice() withImageUrl 'https://s3.amazonaws.com/sitewhere-demo/construction/misc/android-logo.png'
assetType.withDescription 'This thin, lightweight Android tablet features a 7-inch touch display along with the same familiar interface as other Samsung Galaxy devices, making it easy to use. Use it to quickly browse the web, watch movies, read e-books, or download apps from Google Play.'
assetType.metadata 'manufacturer', 'Samsung' metadata 'cpu', '1.2ghz' metadata 'memory', '1gb'
assetBuilder.persist assetType

assetType = assetBuilder.newAssetType 'raspberrypi', 'Raspberry Pi'
assetType.asDevice() withImageUrl 'https://s3.amazonaws.com/sitewhere-demo/construction/misc/raspberry-pi.jpg'
assetType.withDescription 'The Raspberry Pi is a credit-card-sized single-board computer developed in the UK by the Raspberry Pi Foundation with the intention of promoting the teaching of basic computer science in schools.'
assetType.metadata 'manufacturer', 'Raspberry Pi Foundation' metadata 'weight', '1.000' metadata 'memory', '2kb'
assetBuilder.persist assetType

assetType = assetBuilder.newAssetType 'mt90', 'MeiTrack MT90'
assetType.asDevice() withImageUrl 'https://s3.amazonaws.com/sitewhere-demo/construction/meitrack/mt90.jpg'
assetType.withDescription 'MT90 is a waterproof GPS personal tracker suitable for lone workers, kids, aged, pet, assets, vehicle and fleet management.'
assetType.metadata 'manufacturer', 'MeiTrack' metadata 'weight', '1.000' metadata 'memory', '8kb'
assetBuilder.persist assetType

assetType = assetBuilder.newAssetType 'gw1', 'Gateway Device'
assetType.asDevice() withImageUrl 'https://s3.amazonaws.com/sitewhere-demo/construction/misc/gateway.gif'
assetType.withDescription 'Sample gateway for testing nested device configurations.'
assetType.metadata 'manufacturer', 'Advantech'
assetBuilder.persist assetType

assetType = assetBuilder.newAssetType 'uno', 'Arduino Uno'
assetType.asDevice() withImageUrl 'https://s3.amazonaws.com/sitewhere-demo/construction/arduino/uno.jpg'
assetType.withDescription 'The Arduino Uno is a microcontroller board based on the ATmega328.'
assetType.metadata 'manufacturer', 'Arduino'
assetBuilder.persist assetType

assetType = assetBuilder.newAssetType 'mega2560', 'Arduino Mega 2560'
assetType.asDevice() withImageUrl 'https://s3.amazonaws.com/sitewhere-demo/construction/arduino/mega2560.jpg'
assetType.withDescription 'The Arduino Mega 2560 is a microcontroller board based on the ATmega2560.'
assetType.metadata 'manufacturer', 'Arduino'
assetBuilder.persist assetType

assetType = assetBuilder.newAssetType 'openhab', 'openHAB Virtual Device'
assetType.asDevice() withImageUrl 'https://s3.amazonaws.com/sitewhere-demo/gateway/openhab.png'
assetType.withDescription 'This is a virual device type for testing openHAB functionality.'
assetType.metadata 'manufacturer', 'openHAB'
assetBuilder.persist assetType

assetType = assetBuilder.newAssetType 'nodered', 'Node-RED Virtual Device'
assetType.asDevice() withImageUrl 'https://s3.amazonaws.com/sitewhere-demo/gateway/node-red.png'
assetType.withDescription 'This is a virual device type for testing Node-RED functionality.'
assetType.metadata 'manufacturer', 'Node-RED'
assetBuilder.persist assetType

assetType = assetBuilder.newAssetType 'ekahau-a4', 'Ekahau A4 Tag'
assetType.asDevice() withImageUrl 'https://s3.amazonaws.com/sitewhere-demo/construction/ekahau/ekahau-a4-tag.jpg'
assetType.withDescription 'A4 Wi-Fi tags act as small computers that transmit data to the Ekahau Vision server which determines asset location and status, displayed in the Vision software interface.'
assetBuilder.persist assetType

assetType = assetBuilder.newAssetType 'ekahau-b4', 'Ekahau B4 Tag'
assetType.asDevice() withImageUrl 'https://s3.amazonaws.com/sitewhere-demo/construction/ekahau/ekahau-b4-tag.jpg'
assetType.withDescription 'B4 badge tags offer an integrated staff duress or panic switch--employees just tug on the badge and their location and an alarm is sent to security and other designated groups.'
assetType.metadata 'manufacturer', 'Ekahau'
assetBuilder.persist assetType

assetType = assetBuilder.newAssetType 'ekahau-T301W', 'Ekahau T301W Wearable Tag'
assetType.asDevice() withImageUrl 'https://s3.amazonaws.com/sitewhere-demo/construction/ekahau/ekahau-t301w-wearable-tag.jpg'
assetType.withDescription 'The waterproof and wearable T301W wristband tag includes two colored LEDs which signal events to the tag wearer, in addition to vibration-based alerting.'
assetType.metadata 'manufacturer', 'Ekahau'
assetBuilder.persist assetType

assetType = assetBuilder.newAssetType 'ekahau-TS1', 'Ekahau Wireless TS1 Temperature Sensor'
assetType.asDevice() withImageUrl 'https://s3.amazonaws.com/sitewhere-demo/construction/ekahau/ekahau-ts1.jpg'
assetType.withDescription 'The lightweight TS1 temperature tag with probe measures and communicates temperatures ranging from: -80� C - + 125� C, and custom high/low threshold settings. TS1 sensors come with a programmable on-board audio buzzer and visual LED alert buttons.'
assetType.metadata 'manufacturer', 'Ekahau'
assetBuilder.persist assetType

assetType = assetBuilder.newAssetType 'ekahau-TS2', 'Ekahau Wireless TS2 Temperature Sensor'
assetType.asDevice() withImageUrl 'https://s3.amazonaws.com/sitewhere-demo/construction/ekahau/ekahau-ts1.jpg'
assetType.withDescription 'With the TS2 Dual Temperature Sensor tag, you can monitor two separate probes from the same sensor which lowers the total cost of deployment. The temperature sensor tag itself remains always outside of the extreme temperatures allowing for easy maintenance and better network connectivity. In case of network outages, the Ekahau TS tags can locally store the temperature measurements and automatically provide the measurements to the application after the outage.'
assetType.metadata 'manufacturer', 'Ekahau'
assetBuilder.persist assetType

assetType = assetBuilder.newAssetType 'ekahau-HS1', 'Ekahau HS1 Humidity Sensor'
assetType.asDevice() withImageUrl 'https://s3.amazonaws.com/sitewhere-demo/construction/ekahau/ekahau-humidity-sensor.jpg'
assetType.withDescription 'The Ekahau HS1 humidity tags provide an automated way to measure and monitor the relative humidity and surrouding temperature.'
assetType.metadata 'manufacturer', 'Ekahau'
assetBuilder.persist assetType

assetType = assetBuilder.newAssetType 'laipac-S911', 'S911 Bracelet Locator HC'
assetType.asDevice() withImageUrl 'https://s3.amazonaws.com/sitewhere-demo/construction/laipac/laipac-s-911bl.png'
assetType.withDescription 'S911 Bracelet Locator HC (Healthcare) can be used to provide location of the patients, physicians, nurses, and police on duty and assist patients unable to communicate due to issues of injury, health or age.'
assetType.metadata 'manufacturer', 'Ekahau'
assetBuilder.persist assetType

assetType = assetBuilder.newAssetType 'iphone6s', 'Apple iPhone 6S'
assetType.asDevice() withImageUrl 'https://s3.amazonaws.com/sitewhere-demo/construction/misc/iphone6.jpg'
assetType.withDescription "The only thing that's changed is everything. The moment you use iPhone 6s, you know you've never felt anything like it. With just a single press, 3D Touch lets you do more than ever. Live Photos brings your memories to life in a powerfully vivid way. And that's just the beginning. Take a deeper look at iPhone 6s, and you'll find innovation on every level."
assetType.metadata 'manufacturer', 'Apple'
assetBuilder.persist assetType

assetType = assetBuilder.newAssetType 'ipad', 'Apple iPad'
assetType.asDevice() withImageUrl 'https://s3.amazonaws.com/sitewhere-demo/construction/misc/ipad.jpg'
assetType.withDescription "Even with its 12.9-inch Retina display, the largest and most capable iPad ever is only 6.9mm thin and weighs just 1.57 lbs. It has a powerful A9X chip with 64-bit desktop-class architecture, four speaker audio, advanced iSight and FaceTime HD cameras, Wi-Fi and LTE connectivity, iCloud, the breakthrough Touch ID fingerprint sensor, and up to 10 hours of battery life."
assetType.metadata 'manufacturer', 'Apple'
assetBuilder.persist assetType

// ########################### //
// Create Hardware Asset Types //
// ########################### //

assetType = assetBuilder.newAssetType 'cat416f', 'Caterpillar 416F Backhoe Loader'
assetType.asHardware() withImageUrl 'https://s3.amazonaws.com/sitewhere-demo/construction/cat/cat-416f.jpg'
assetType.withDescription "Cat's Backhoe Loaders set the industry standard for operator comfort, exceptional performance, versatility and jobsite efficiency."
assetType.metadata 'manufacturer', 'Caterpillar'
assetBuilder.persist assetType

assetType = assetBuilder.newAssetType 'cat430f', 'Caterpillar 430F Backhoe Loader'
assetType.asHardware() withImageUrl 'https://s3.amazonaws.com/sitewhere-demo/construction/cat/cat-430f.jpg'
assetType.withDescription "Cat's Backhoe Loaders set the industry standard for operator comfort, exceptional performance, versatility and jobsite efficiency."
assetType.metadata 'manufacturer', 'Caterpillar'
assetBuilder.persist assetType

assetType = assetBuilder.newAssetType 'catD5K2', 'Caterpillar D5K2 Dozer'
assetType.asHardware() withImageUrl 'https://s3.amazonaws.com/sitewhere-demo/construction/cat/cat-d5k2.jpg'
assetType.withDescription "Cat's small dozers are designed to optimize speed, transportability, maneuverability, versatility and finish grading accuracy. These crawler dozers are ideal for residential construction performing such tasks as clearing and grading lots, sloping the sides of roads, back-filling, and final grade work for landscaping and driveway construction."
assetType.metadata 'manufacturer', 'Caterpillar'
assetBuilder.persist assetType

assetType = assetBuilder.newAssetType 'cat320EL', 'Caterpillar 320E L Excavator'
assetType.asHardware() withImageUrl 'https://s3.amazonaws.com/sitewhere-demo/construction/cat/cat-320e.jpg'
assetType.withDescription "A great all-around excavator, the 320E is easy to maneuver, yet powerful enough to handle tough jobs. Lift capacity has been improved by 5 percent on the standard machine and up to 20 percent on the Heavy Lift configuration. A lower emissions engine boosts fuel efficiency, while other new features enhance safety and productivity, cut service time and reduce operating costs."
assetType.metadata 'manufacturer', 'Caterpillar'
assetBuilder.persist assetType

assetType = assetBuilder.newAssetType 'cat324E', 'Caterpillar 324E Excavator'
assetType.asHardware() withImageUrl 'https://s3.amazonaws.com/sitewhere-demo/construction/cat/cat-324e.jpg'
assetType.withDescription "The new 324E offers enhanced versatility and maneuverability. So it's a great option for tight spaces where controllability is critical. Plus, the 324E has more horsepower and operator station improvements that will keep your team working productively and comfortably all shift long."
assetType.metadata 'manufacturer', 'Caterpillar'
assetBuilder.persist assetType


def asset;

// #################### //
// Create Person Assets //
// #################### //
asset = assetBuilder.newAsset 'derek.adams@sitewhere.com', 'employee', 'Derek Adams'
asset.withImageUrl 'https://s3.amazonaws.com/sitewhere-demo/people/derek.jpg'
asset.metadata 'username', 'derek' metadata 'role', 'dev' metadata 'phone', '678-555-1212'
assetBuilder.persist asset

asset = assetBuilder.newAsset 'bryan.rank@sitewhere.com', 'employee', 'Bryan Rank'
asset.withImageUrl 'https://s3.amazonaws.com/sitewhere-demo/people/bryan.jpg'
asset.metadata 'username', 'bryan' metadata 'role', 'sales' metadata 'phone', '770-555-1212'
assetBuilder.persist asset

asset = assetBuilder.newAsset 'martin.weber@sitewhere.com', 'employee', 'Martin Weber'
asset.withImageUrl 'https://s3.amazonaws.com/sitewhere-demo/people/martin.jpg'
asset.metadata 'username', 'martin' metadata 'role', 'busdev' metadata 'phone', '404-555-1212'
assetBuilder.persist asset

// #################### //
// Create Device Assets //
// #################### //

asset = assetBuilder.newAsset '120381238-SERIAL-NUMBER-GALAXYTAB3', 'galaxytab3', 'Galaxy Phone 1'
asset.withImageUrl 'https://s3.amazonaws.com/sitewhere-demo/construction/misc/android-logo.png'
assetBuilder.persist asset

asset = assetBuilder.newAsset '234209937-SERIAL-NUMBER-RASPI', 'raspberrypi', 'Raspberry Pi 1'
asset.withImageUrl 'https://s3.amazonaws.com/sitewhere-demo/construction/misc/raspberry-pi.jpg'
assetBuilder.persist asset
 
asset = assetBuilder.newAsset '234057124-SERIAL-NUMBER-MT90', 'mt90', 'MT90 Tracker 1'
asset.withImageUrl 'https://s3.amazonaws.com/sitewhere-demo/construction/meitrack/mt90.jpg'
assetBuilder.persist asset
 
asset = assetBuilder.newAsset '548022735-SERIAL-NUMBER-GW1', 'gw1', 'Gateway Test 1'
asset.withImageUrl 'https://s3.amazonaws.com/sitewhere-demo/construction/misc/gateway.gif'
assetBuilder.persist asset
 
asset = assetBuilder.newAsset '345820348-SERIAL-NUMBER-UNO', 'uno', 'Arduino Uno 1'
asset.withImageUrl 'https://s3.amazonaws.com/sitewhere-demo/construction/arduino/uno.jpg'
assetBuilder.persist asset
 
asset = assetBuilder.newAsset '523483499-SERIAL-NUMBER-MEGA', 'mega2560', 'Arduino Mega2560 1'
asset.withImageUrl 'https://s3.amazonaws.com/sitewhere-demo/construction/arduino/mega2560.jpg'
assetBuilder.persist asset
 
asset = assetBuilder.newAsset '872349273-SERIAL-NUMBER-OHAB', 'openhab', 'openHAB Test Device 1'
asset.withImageUrl 'https://s3.amazonaws.com/sitewhere-demo/gateway/openhab.png'
assetBuilder.persist asset
 
asset = assetBuilder.newAsset '193743433-SERIAL-NUMBER-NRED', 'nodered', 'Node-RED Test Device 1'
asset.withImageUrl 'https://s3.amazonaws.com/sitewhere-demo/gateway/node-red.png'
assetBuilder.persist asset
 
asset = assetBuilder.newAsset '342349343-SERIAL-NUMBER-EKA4', 'ekahau-a4', 'Ekakau A4 1'
asset.withImageUrl 'https://s3.amazonaws.com/sitewhere-demo/construction/ekahau/ekahau-a4-tag.jpg'
assetBuilder.persist asset
 
asset = assetBuilder.newAsset '623947324-SERIAL-NUMBER-EKB4', 'ekahau-b4', 'Ekakau B4 1'
asset.withImageUrl 'https://s3.amazonaws.com/sitewhere-demo/construction/ekahau/ekahau-b4-tag.jpg'
assetBuilder.persist asset
 
asset = assetBuilder.newAsset '392455494-SERIAL-NUMBER-T301W', 'ekahau-T301W', 'Ekakau T301W 1'
asset.withImageUrl 'https://s3.amazonaws.com/sitewhere-demo/construction/ekahau/ekahau-t301w-wearable-tag.jpg'
assetBuilder.persist asset
 
asset = assetBuilder.newAsset '734539339-SERIAL-NUMBER-TS1', 'ekahau-TS1', 'Ekakau TS1 1'
asset.withImageUrl 'https://s3.amazonaws.com/sitewhere-demo/construction/ekahau/ekahau-ts1.jpg'
assetBuilder.persist asset
 
asset = assetBuilder.newAsset '193835744-SERIAL-NUMBER-TS1', 'ekahau-TS2', 'Ekakau TS2 1'
asset.withImageUrl 'https://s3.amazonaws.com/sitewhere-demo/construction/ekahau/ekahau-ts1.jpg'
assetBuilder.persist asset
 
asset = assetBuilder.newAsset '398434398-SERIAL-NUMBER-HS1', 'ekahau-HS1', 'Ekakau HS1 1'
asset.withImageUrl 'https://s3.amazonaws.com/sitewhere-demo/construction/ekahau/ekahau-humidity-sensor.jpg'
assetBuilder.persist asset
 
asset = assetBuilder.newAsset '239437373-SERIAL-NUMBER-S911', 'laipac-S911', 'Laipac S911 1'
asset.withImageUrl 'https://s3.amazonaws.com/sitewhere-demo/construction/laipac/laipac-s-911bl.png'
assetBuilder.persist asset
 
asset = assetBuilder.newAsset '823473454-SERIAL-NUMBER-IP6S', 'iphone6s', 'iPhone 6S 1'
asset.withImageUrl 'https://s3.amazonaws.com/sitewhere-demo/construction/misc/iphone6.jpg'
assetBuilder.persist asset
 
asset = assetBuilder.newAsset '682374234-SERIAL-NUMBER-IPAD', 'ipad', 'iPad 1'
asset.withImageUrl 'https://s3.amazonaws.com/sitewhere-demo/construction/misc/ipad.jpg'
assetBuilder.persist asset

// ###################### //
// Create Hardware Assets //
// ###################### //
 
asset = assetBuilder.newAsset '923483933-SERIAL-NUMBER-416F', 'cat416f', 'Cat 416F 1'
asset.withImageUrl 'https://s3.amazonaws.com/sitewhere-demo/construction/cat/cat-416f.jpg'
assetBuilder.persist asset
 
asset = assetBuilder.newAsset '298383493-SERIAL-NUMBER-430F', 'cat430f', 'Cat 430F 1'
asset.withImageUrl 'https://s3.amazonaws.com/sitewhere-demo/construction/cat/cat-430f.jpg'
assetBuilder.persist asset
 
asset = assetBuilder.newAsset '593434849-SERIAL-NUMBER-D5K2', 'catD5K2', 'Cat D5K2 1'
asset.withImageUrl 'https://s3.amazonaws.com/sitewhere-demo/construction/cat/cat-d5k2.jpg'
assetBuilder.persist asset
 
asset = assetBuilder.newAsset '345438345-SERIAL-NUMBER-D5K2', 'catD5K2', 'Cat D5K2 2'
asset.withImageUrl 'https://s3.amazonaws.com/sitewhere-demo/construction/cat/cat-d5k2.jpg'
assetBuilder.persist asset
 
asset = assetBuilder.newAsset '847234833-SERIAL-NUMBER-320EL', 'cat320EL', 'Cat 320E L 1'
asset.withImageUrl 'https://s3.amazonaws.com/sitewhere-demo/construction/cat/cat-320e.jpg'
assetBuilder.persist asset
 
asset = assetBuilder.newAsset '349544949-SERIAL-NUMBER-324E', 'cat324E', 'Cat 324E 1'
asset.withImageUrl 'https://s3.amazonaws.com/sitewhere-demo/construction/cat/cat-324e.jpg'
assetBuilder.persist asset
