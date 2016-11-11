// ####################### //
// Create Asset Categories //
// ####################### //
def atdevices = assetBuilder.newAssetCategory 'at-devices', 'Air Traffic Devices' forDeviceAssets()
atdevices = assetBuilder.persist atdevices

def atplanes = assetBuilder.newAssetCategory 'at-planes', 'Air Traffic Planes' forHardwareAssets()
atplanes = assetBuilder.persist atplanes

def asset;

// ##################### //
// Create Tracker Assets //
// ##################### //

asset = assetBuilder.newHardwareAsset 'FMZ2000', 'FMZ 2000', 'https://s3.amazonaws.com/sitewhere-demo/airport/fmz2000.jpg' withSku 'FMZ2000' 
asset.withDescription 'Flight Management System of choice for pilots and OEMs for nearly 20 years. Today there are well over 3,000 aircraft in service with the FMZ-2000.'
asset.withProperty 'manufacturer', 'Honeywell'
assetBuilder.persist atdevices.id, asset

// ################### //
// Create Plane Assets //
// ################### //

asset = assetBuilder.newHardwareAsset 'A330-200', 'Airbus A330-200', 'https://s3.amazonaws.com/sitewhere-demo/airport/a330-200.jpg' withSku 'A330-200' 
asset.withDescription "The Airbus A330 is a medium wide-body twin-engine jet airliner made by Airbus, a division of Airbus Group."
asset.withProperty 'manufacturer', 'Airbus' withProperty 'passengers', '234' withProperty 'cruiseSpeed', '567' withProperty 'range', '7021'
assetBuilder.persist atplanes.id, asset

asset = assetBuilder.newHardwareAsset 'A330-300', 'Airbus A330-300', 'https://s3.amazonaws.com/sitewhere-demo/airport/a330-300.jpg' withSku 'A330-300' 
asset.withDescription "The Airbus A330 is a medium wide-body twin-engine jet airliner made by Airbus, a division of Airbus Group."
asset.withProperty 'manufacturer', 'Airbus' withProperty 'passengers', '293' withProperty 'cruiseSpeed', '567' withProperty 'range', '7021'
assetBuilder.persist atplanes.id, asset

asset = assetBuilder.newHardwareAsset 'BOEING-717', 'Boeing 717', 'https://s3.amazonaws.com/sitewhere-demo/airport/boeing717.jpg' withSku 'BOEING-717' 
asset.withDescription "The Boeing 717 is a twin-engine, single-aisle jet airliner, developed for the 100-seat market. The airliner was designed and originally marketed by McDonnell Douglas as the MD-95, a derivative of the DC-9 family."
asset.withProperty 'manufacturer', 'Boeing' withProperty 'passengers', '110' withProperty 'cruiseSpeed', '504' withProperty 'range', '2371'
assetBuilder.persist atplanes.id, asset

asset = assetBuilder.newHardwareAsset 'BOEING-737', 'Boeing 737-700', 'https://s3.amazonaws.com/sitewhere-demo/airport/boeing737.jpg' withSku 'BOEING-737' 
asset.withDescription "The Boeing 737 is a short- to medium-range twinjet narrow-body airliner. Originally developed as a shorter, lower-cost twin-engined airliner derived from Boeing's 707 and 727, the 737 has developed into a family of nine passenger models with a capacity of 85 to 215 passengers."
asset.withProperty 'manufacturer', 'Boeing' withProperty 'passengers', '124' withProperty 'cruiseSpeed', '624' withProperty 'range', '4400'
assetBuilder.persist atplanes.id, asset
