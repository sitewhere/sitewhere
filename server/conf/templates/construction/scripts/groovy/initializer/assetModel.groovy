// ############################## //
// Create Device Asset Categories //
// ############################## //
def persons = assetBuilder.newAssetCategory 'fs-persons', 'Default Identity Management' forPersonAssets()
persons = assetBuilder.persist persons

def devices = assetBuilder.newAssetCategory 'fs-devices', 'Default Device Management' forDeviceAssets()
devices = assetBuilder.persist devices

def hardware = assetBuilder.newAssetCategory 'fs-hardware', 'Default Hardware Management' forHardwareAssets()
hardware = assetBuilder.persist hardware

def locations = assetBuilder.newAssetCategory 'fs-locations', 'Default Location Management' forLocationAssets()
locations = assetBuilder.persist locations

def asset;

// #################### //
// Create Person Assets //
// #################### //

asset = assetBuilder.newPersonAsset '1', 'Derek Adams', 'https://s3.amazonaws.com/sitewhere-demo/people/derek.jpg'
asset.withUsername 'derek' withEmailAddress 'derek.adams@sitewhere.com' withRole 'dev'
asset.withProperty 'phone', '678-555-1212'
assetBuilder.persist persons.id, asset

asset = assetBuilder.newPersonAsset '2', 'Bryan Rank', 'https://s3.amazonaws.com/sitewhere-demo/people/bryan.jpg'
asset.withUsername 'bryan' withEmailAddress 'bryan.rank@sitewhere.com' withRole 'techsales' withRole 'dev'
asset.withProperty 'phone', '770-555-1212'
assetBuilder.persist persons.id, asset

asset = assetBuilder.newPersonAsset '3', 'Martin Weber', 'https://s3.amazonaws.com/sitewhere-demo/people/martin.jpg'
asset.withUsername 'martin' withEmailAddress 'martin.weber@sitewhere.com' withRole 'busdev' withRole 'dev'
asset.withProperty 'phone', '404-555-1212'
assetBuilder.persist persons.id, asset

// #################### //
// Create Device Assets //
// #################### //

asset = assetBuilder.newHardwareAsset '173', 'Galaxy Tab 3', 'https://s3.amazonaws.com/sitewhere-demo/construction/misc/android-logo.png' withSku 'GTAB3' 
asset.withDescription 'This thin, lightweight Android tablet features a 7-inch touch display along with the same familiar interface as other Samsung Galaxy devices, making it easy to use. Use it to quickly browse the web, watch movies, read e-books, or download apps from Google Play.'
asset.withProperty 'manufacturer', 'Samsung' withProperty 'cpu', 'Dual Core Application Processor' withProperty 'sd.size', '1gb'
assetBuilder.persist devices.id, asset

asset = assetBuilder.newHardwareAsset '174', 'Raspberry Pi', 'https://s3.amazonaws.com/sitewhere-demo/construction/misc/raspberry-pi.jpg' withSku 'RPI' 
asset.withDescription 'The Raspberry Pi is a credit-card-sized single-board computer developed in the UK by the Raspberry Pi Foundation with the intention of promoting the teaching of basic computer science in schools.'
asset.withProperty 'manufacturer', 'Raspberry Pi Foundation' withProperty 'cpu', 'ARM1176JZF-S' withProperty 'sd.size', '32gb'
assetBuilder.persist devices.id, asset

asset = assetBuilder.newHardwareAsset '175', 'MeiTrack MT90', 'https://s3.amazonaws.com/sitewhere-demo/construction/meitrack/mt90.jpg' withSku 'MT90' 
asset.withDescription 'MT90 is a waterproof GPS personal tracker suitable for lone workers, kids, aged, pet, assets, vehicle and fleet management.'
asset.withProperty 'manufacturer', 'MeiTrack' withProperty 'weight', '1.000' withProperty 'sos.button', 'true' withProperty 'two.way.audio', 'false'
assetBuilder.persist devices.id, asset

asset = assetBuilder.newHardwareAsset '176', 'Gateway Device', 'https://s3.amazonaws.com/sitewhere-demo/construction/misc/gateway.gif' withSku 'GW1' 
asset.withDescription 'Sample gateway for testing nested device configurations.'
asset.withProperty 'manufacturer', 'Advantech'
assetBuilder.persist devices.id, asset

asset = assetBuilder.newHardwareAsset '180', 'Arduino Uno', 'https://s3.amazonaws.com/sitewhere-demo/construction/arduino/uno.jpg' withSku 'UNO' 
asset.withDescription 'The Arduino Uno is a microcontroller board based on the ATmega328.'
asset.withProperty 'manufacturer', 'Arduino' withProperty 'operating.voltage', '5V'
assetBuilder.persist devices.id, asset

asset = assetBuilder.newHardwareAsset '181', 'Arduino Mega 2560', 'https://s3.amazonaws.com/sitewhere-demo/construction/arduino/mega2560.jpg' withSku 'M2560' 
asset.withDescription 'The Arduino Mega 2560 is a microcontroller board based on the ATmega2560.'
asset.withProperty 'manufacturer', 'Arduino' withProperty 'operating.voltage', '5V'
assetBuilder.persist devices.id, asset

asset = assetBuilder.newHardwareAsset '190', 'openHAB Virtual Device', 'https://s3.amazonaws.com/sitewhere-demo/gateway/openhab.png' withSku 'OHAB' 
asset.withDescription 'The Arduino Mega 2560 is a microcontroller board based on the ATmega2560.'
asset.withProperty 'manufacturer', 'openHAB'
assetBuilder.persist devices.id, asset

asset = assetBuilder.newHardwareAsset '195', 'Node-RED Virtual Device', 'https://s3.amazonaws.com/sitewhere-demo/gateway/node-red.png' withSku 'NRED' 
asset.withDescription 'The Arduino Mega 2560 is a microcontroller board based on the ATmega2560.'
asset.withProperty 'manufacturer', 'Node-RED'
assetBuilder.persist devices.id, asset

asset = assetBuilder.newHardwareAsset '200', 'Ekahau A4 Tag', 'https://s3.amazonaws.com/sitewhere-demo/construction/ekahau/ekahau-a4-tag.jpg' withSku 'EA4' 
asset.withDescription 'A4 Wi-Fi tags act as small computers that transmit data to the Ekahau Vision server which determines asset location and status, displayed in the Vision software interface.'
asset.withProperty 'manufacturer', 'Ekahau' withProperty 'wifi', 'true' withProperty 'programmable.buttons', 'true'
asset.withProperty 'remote.audio', 'true' withProperty 'wearable', 'false'
assetBuilder.persist devices.id, asset

asset = assetBuilder.newHardwareAsset '201', 'Ekahau B4 Tag', 'https://s3.amazonaws.com/sitewhere-demo/construction/ekahau/ekahau-b4-tag.jpg' withSku 'EB4' 
asset.withDescription 'B4 badge tags offer an integrated staff duress or panic switch--employees just tug on the badge and their location and an alarm is sent to security and other designated groups.'
asset.withProperty 'manufacturer', 'Ekahau' withProperty 'wifi', 'true' withProperty 'programmable.buttons', 'true'
asset.withProperty 'remote.audio', 'false' withProperty 'wearable', 'true' withProperty 'senses.temperature', 'false'
assetBuilder.persist devices.id, asset

asset = assetBuilder.newHardwareAsset '202', 'Ekahau T301W Wearable Tag', 'https://s3.amazonaws.com/sitewhere-demo/construction/ekahau/ekahau-t301w-wearable-tag.jpg' withSku 'ET301W' 
asset.withDescription 'The waterproof and wearable T301W wristband tag includes two colored LEDs which signal events to the tag wearer, in addition to vibration-based alerting.'
asset.withProperty 'manufacturer', 'Ekahau' withProperty 'wifi', 'true' withProperty 'programmable.buttons', 'false'
asset.withProperty 'remote.audio', 'false' withProperty 'wearable', 'true' withProperty 'senses.temperature', 'false'
assetBuilder.persist devices.id, asset

asset = assetBuilder.newHardwareAsset '203', 'Ekahau Wireless TS1 Temperature Sensor', 'https://s3.amazonaws.com/sitewhere-demo/construction/ekahau/ekahau-ts1.jpg' withSku 'ETS1' 
asset.withDescription 'The lightweight TS1 temperature tag with probe measures and communicates temperatures ranging from: -80° C - + 125° C, and custom high/low threshold settings. TS1 sensors come with a programmable on-board audio buzzer and visual LED alert buttons.'
asset.withProperty 'manufacturer', 'Ekahau' withProperty 'wifi', 'true' withProperty 'senses.temperature', 'true'
asset.withProperty 'senses.humidity', 'false'
assetBuilder.persist devices.id, asset

asset = assetBuilder.newHardwareAsset '204', 'Ekahau Wireless TS2 Temperature Sensor', 'https://s3.amazonaws.com/sitewhere-demo/construction/ekahau/ekahau-ts1.jpg' withSku 'ETS2' 
asset.withDescription 'With the TS2 Dual Temperature Sensor tag, you can monitor two separate probes from the same sensor which lowers the total cost of deployment. The temperature sensor tag itself remains always outside of the extreme temperatures allowing for easy maintenance and better network connectivity. In case of network outages, the Ekahau TS tags can locally store the temperature measurements and automatically provide the measurements to the application after the outage.'
asset.withProperty 'manufacturer', 'Ekahau' withProperty 'wifi', 'true' withProperty 'senses.temperature', 'true'
asset.withProperty 'senses.humidity', 'false'
assetBuilder.persist devices.id, asset

asset = assetBuilder.newHardwareAsset '205', 'Ekahau HS1 Humidity Sensor', 'https://s3.amazonaws.com/sitewhere-demo/construction/ekahau/ekahau-humidity-sensor.jpg' withSku 'EHS1' 
asset.withDescription 'The Ekahau HS1 humidity tags provide an automated way to measure and monitor the relative humidity and surrouding temperature.'
asset.withProperty 'manufacturer', 'Ekahau' withProperty 'wifi', 'true' withProperty 'senses.temperature', 'false'
asset.withProperty 'senses.humidity', 'true'
assetBuilder.persist devices.id, asset

asset = assetBuilder.newHardwareAsset '300', 'S911 Bracelet Locator HC', 'https://s3.amazonaws.com/sitewhere-demo/construction/laipac/laipac-s-911bl.png' withSku 'S911HC' 
asset.withDescription 'S911 Bracelet Locator HC (Healthcare) can be used to provide location of the patients, physicians, nurses, and police on duty and assist patients unable to communicate due to issues of injury, health or age.'
asset.withProperty 'manufacturer', 'Laipac'
assetBuilder.persist devices.id, asset

asset = assetBuilder.newHardwareAsset '400', 'Apple iPhone', 'https://s3.amazonaws.com/sitewhere-demo/construction/misc/iphone6.jpg' withSku 'AAPL-IPHONE6S' 
asset.withDescription "The only thing that's changed is everything. The moment you use iPhone 6s, you know you've never felt anything like it. With just a single press, 3D Touch lets you do more than ever. Live Photos brings your memories to life in a powerfully vivid way. And that's just the beginning. Take a deeper look at iPhone 6s, and you'll find innovation on every level."
asset.withProperty 'manufacturer', 'Apple'
assetBuilder.persist devices.id, asset

asset = assetBuilder.newHardwareAsset '405', 'Apple iPad', 'https://s3.amazonaws.com/sitewhere-demo/construction/misc/ipad.jpg' withSku 'AAPL-IPAD' 
asset.withDescription 'Even with its 12.9-inch Retina display, the largest and most capable iPad ever is only 6.9mm thin and weighs just 1.57 lbs. It has a powerful A9X chip with 64-bit desktop-class architecture, four speaker audio, advanced iSight and FaceTime HD cameras, Wi-Fi and LTE connectivity, iCloud, the breakthrough Touch ID fingerprint sensor, and up to 10 hours of battery life.'
asset.withProperty 'manufacturer', 'Apple'
assetBuilder.persist devices.id, asset

// ###################### //
// Create Hardware Assets //
// ###################### //

asset = assetBuilder.newHardwareAsset '300', 'Caterpillar 416F Backhoe Loader', 'https://s3.amazonaws.com/sitewhere-demo/construction/cat/cat-416f.jpg' withSku 'CAT-416F' 
asset.withDescription "Cat® Backhoe Loaders set the industry standard for operator comfort, exceptional performance, versatility and jobsite efficiency."
asset.withProperty 'manufacturer', 'Caterpillar' withProperty 'net.power', '87' withProperty 'operating.weight', '24251'
asset.withProperty 'dig.depth', '14.3'
assetBuilder.persist hardware.id, asset

asset = assetBuilder.newHardwareAsset '301', 'Caterpillar 430F Backhoe Loader', 'https://s3.amazonaws.com/sitewhere-demo/construction/cat/cat-430f.jpg' withSku 'CAT-430F' 
asset.withDescription "Cat® Backhoe Loaders set the industry standard for operator comfort, exceptional performance, versatility and jobsite efficiency."
asset.withProperty 'manufacturer', 'Caterpillar' withProperty 'net.power', '107' withProperty 'operating.weight', '24251'
asset.withProperty 'dig.depth', '15.4'
assetBuilder.persist hardware.id, asset

asset = assetBuilder.newHardwareAsset '302', 'Caterpillar D5K2 Dozer', 'https://s3.amazonaws.com/sitewhere-demo/construction/cat/cat-d5k2.jpg' withSku 'CAT-D5K2' 
asset.withDescription "The Cat® small dozers are designed to optimize speed, transportability, maneuverability, versatility and finish grading accuracy. These crawler dozers are ideal for residential construction performing such tasks as clearing and grading lots, sloping the sides of roads, back-filling, and final grade work for landscaping and driveway construction."
asset.withProperty 'manufacturer', 'Caterpillar' withProperty 'net.power', '104' withProperty 'operating.weight', '20534'
asset.withProperty 'fuel.tank', '51.5'
assetBuilder.persist hardware.id, asset

asset = assetBuilder.newHardwareAsset '303', 'Caterpillar 320E L Excavator', 'https://s3.amazonaws.com/sitewhere-demo/construction/cat/cat-320e.jpg' withSku 'CAT-320E-L' 
asset.withDescription "A great all-around excavator, the 320E is easy to maneuver, yet powerful enough to handle tough jobs. Lift capacity has been improved by 5 percent on the standard machine and up to 20 percent on the Heavy Lift configuration. A lower emissions engine boosts fuel efficiency, while other new features enhance safety and productivity, cut service time and reduce operating costs."
asset.withProperty 'manufacturer', 'Caterpillar' withProperty 'net.power', '153' withProperty 'operating.weight', '54450'
asset.withProperty 'fuel.tank', '108.3'
assetBuilder.persist hardware.id, asset

asset = assetBuilder.newHardwareAsset '304', 'Caterpillar 324E Excavator', 'https://s3.amazonaws.com/sitewhere-demo/construction/cat/cat-324e.jpg' withSku 'CAT-324E' 
asset.withDescription "The new 324E offers enhanced versatility and maneuverability. So it's a great option for tight spaces where controllability is critical. Plus, the 324E has more horsepower and operator station improvements that will keep your team working productively and comfortably all shift long."
asset.withProperty 'manufacturer', 'Caterpillar' withProperty 'net.power', '190' withProperty 'operating.weight', '64990'
asset.withProperty 'fuel.tank', '137.37'
assetBuilder.persist hardware.id, asset

// ###################### //
// Create Location Assets //
// ###################### //

asset = assetBuilder.newLocationAsset '1', 'Tool Storage Container', 'https://s3.amazonaws.com/sitewhere-demo/construction/toolbox.png' 
asset.withLatitude 33.7550 withLongitude -84.3900
assetBuilder.persist locations.id, asset

asset = assetBuilder.newLocationAsset '2', 'Construction Trailer', 'https://s3.amazonaws.com/sitewhere-demo/construction/trailer.jpg' 
asset.withLatitude 33.7550 withLongitude -84.3900
assetBuilder.persist locations.id, asset
