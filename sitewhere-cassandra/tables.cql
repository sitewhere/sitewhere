CREATE TYPE IF NOT EXISTS sw_location (
  latitude double,
  longitude double,
  elevation double
)

CREATE TYPE IF NOT EXISTS sw_measurements (
  measurements map<text, double>
)

CREATE TYPE IF NOT EXISTS sw_alert (
  source text,
  level text,
  type text,
  message text
)

CREATE TABLE IF NOT EXISTS events_by_id (
  deviceId uuid,
  eventId uuid,
  alternateId text,
  eventType text,
  assignmentId uuid,
  areaId uuid,
  assetId uuid,
  eventDate timestamp,
  receivedDate timestamp,
  location frozen<sw_location>,
  measurements frozen<sw_measurements>,
  alert frozen<sw_alert>,
  PRIMARY KEY ((deviceId), eventId)
)

CREATE TABLE IF NOT EXISTS events_by_assignment (
  deviceId uuid,
  eventId uuid,
  alternateId text,
  eventType text,
  assignmentId uuid,
  areaId uuid,
  assetId uuid,
  eventDate timestamp,
  receivedDate timestamp,
  location frozen<sw_location>,
  measurements frozen<sw_measurements>,
  alert frozen<sw_alert>,
  PRIMARY KEY ((deviceId), assignmentId, eventDate, eventId)
) WITH CLUSTERING ORDER BY (assignmentId asc, eventDate desc, eventId asc)

CREATE TABLE IF NOT EXISTS events_by_area (
  deviceId uuid,
  eventId uuid,
  alternateId text,
  eventType text,
  assignmentId uuid,
  areaId uuid,
  assetId uuid,
  eventDate timestamp,
  receivedDate timestamp,
  location frozen<sw_location>,
  measurements frozen<sw_measurements>,
  alert frozen<sw_alert>,
  PRIMARY KEY ((deviceId), areaId, eventDate, eventId)
) WITH CLUSTERING ORDER BY (areaId asc, eventDate desc, eventId asc)
