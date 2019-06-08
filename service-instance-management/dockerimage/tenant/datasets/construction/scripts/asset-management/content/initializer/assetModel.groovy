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
