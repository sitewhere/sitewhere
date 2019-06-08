def assetType;

// ######################### //
// Create Person Asset Types //
// ######################### //

assetType = assetBuilder.newAssetType 'employee', 'SiteWhere Employee'
assetType.asPerson() withImageUrl 'https://s3.amazonaws.com/sitewhere-demo/sitewhere-symbol.png'
assetType.withDescription 'An employee of SiteWhere'
assetBuilder.persist assetType

// ##################### //
// Create Tracker Asset Type //
// ##################### //

assetType = assetBuilder.newAssetType 'FMZ2000', 'FMZ 2000'
assetType.asDevice() withImageUrl 'https://s3.amazonaws.com/sitewhere-demo/airport/fmz2000.jpg'
assetType.withDescription 'Flight Management System of choice for pilots and OEMs for nearly 20 years. Today there are well over 3,000 aircraft in service with the FMZ-2000.'
assetType.metadata 'manufacturer', 'Honeywell'
assetBuilder.persist assetType

// ####################### //
// Create Plane Asset Type //
// ####################### //

assetType = assetBuilder.newAssetType 'A330-200', 'Airbus A330-200'
assetType.asHardware() withImageUrl 'https://s3.amazonaws.com/sitewhere-demo/airport/a330-200.jpg'
assetType.withDescription 'The Airbus A330 is a medium wide-body twin-engine jet airliner made by Airbus, a division of Airbus Group.'
assetType.metadata 'manufacturer', 'Airbus' metadata 'passengers', '234' metadata 'cruiseSpeed', '567' metadata 'range', '7021'
assetBuilder.persist assetType

assetType = assetBuilder.newAssetType 'A330-300', 'Airbus A330-300'
assetType.asHardware() withImageUrl 'https://s3.amazonaws.com/sitewhere-demo/airport/a330-300.jpg'
assetType.withDescription 'The Airbus A330 is a medium wide-body twin-engine jet airliner made by Airbus, a division of Airbus Group.'
assetType.metadata 'manufacturer', 'Airbus' metadata 'passengers', '293' metadata 'cruiseSpeed', '567' metadata 'range', '7021'
assetBuilder.persist assetType

assetType = assetBuilder.newAssetType 'BOEING-717', 'Boeing 717'
assetType.asHardware() withImageUrl 'https://s3.amazonaws.com/sitewhere-demo/airport/boeing717.jpg'
assetType.withDescription 'The Boeing 717 is a twin-engine, single-aisle jet airliner, developed for the 100-seat market. The airliner was designed and originally marketed by McDonnell Douglas as the MD-95, a derivative of the DC-9 family.'
assetType.metadata 'manufacturer', 'Boeing' metadata 'passengers', '110' metadata 'cruiseSpeed', '504' metadata 'range', '2371'
assetBuilder.persist assetType

assetType = assetBuilder.newAssetType 'BOEING-737', 'Boeing 737-700'
assetType.asHardware() withImageUrl 'https://s3.amazonaws.com/sitewhere-demo/airport/boeing737.jpg'
assetType.withDescription "The Boeing 737 is a short- to medium-range twinjet narrow-body airliner. Originally developed as a shorter, lower-cost twin-engined airliner derived from Boeing's 707 and 727, the 737 has developed into a family of nine passenger models with a capacity of 85 to 215 passengers."
assetType.metadata 'manufacturer', 'Boeing' metadata 'passengers', '124' metadata 'cruiseSpeed', '624' metadata 'range', '4400'
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


// ###################### //
// Create Hardware Assets //
// ###################### //
 
asset = assetBuilder.newAsset '923483933-SERIAL-NUMBER-416F', 'FMZ2000', 'FMZ2000 1'
asset.withImageUrl 'https://s3.amazonaws.com/sitewhere-demo/airport/fmz2000.jpg'
assetBuilder.persist asset
 
asset = assetBuilder.newAsset '298383493-SERIAL-NUMBER-430F', 'A330-200', 'A330-200 1'
asset.withImageUrl 'https://s3.amazonaws.com/sitewhere-demo/airport/a330-200.jpg'
assetBuilder.persist asset
 
asset = assetBuilder.newAsset '593434849-SERIAL-NUMBER-D5K2', 'A330-300', 'A330-300 1'
asset.withImageUrl 'https://s3.amazonaws.com/sitewhere-demo/airport/a330-300.jpg'
assetBuilder.persist asset
 
asset = assetBuilder.newAsset '345438345-SERIAL-NUMBER-D5K2', 'BOEING-717', 'BOEING-717 2'
asset.withImageUrl 'https://s3.amazonaws.com/sitewhere-demo/airport/boeing717.jpg'
assetBuilder.persist asset
 
asset = assetBuilder.newAsset '847234833-SERIAL-NUMBER-320EL', 'BOEING-737', 'BOEING-737 1'
asset.withImageUrl 'https://s3.amazonaws.com/sitewhere-demo/airport/boeing737.jpg'
assetBuilder.persist asset