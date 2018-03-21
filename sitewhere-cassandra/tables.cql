CREATE TABLE events_by_id (
  deviceId uuid,
  eventId uuid,
  alternateId text,
  eventType text,
  assignmentId uuid,
  areaId uuid,
  assetId uuid,
  eventDate timestamp,
  receivedDate timestamp,
  PRIMARY KEY ((deviceId, eventId))
)

CREATE TABLE events_by_assignment (
  deviceId uuid,
  assignmentId uuid,
  eventId uuid,
  alternateId text,
  eventType text,
  areaId uuid,
  assetId uuid,
  eventDate timestamp,
  receivedDate timestamp,
  PRIMARY KEY ((deviceId), assignmentId, timestamp, eventId)
) WITH CLUSTERING ORDER BY (assignmentId asc, timestamp desc, eventId asc)

CREATE TABLE events_by_area (
  deviceId uuid,
  areaId uuid,
  eventId uuid,
  alternateId text,
  eventType text,
  assignmentId uuid,
  assetId uuid,
  eventDate timestamp,
  receivedDate timestamp,
  PRIMARY KEY ((deviceId), assignmentId, timestamp, eventId)
) WITH CLUSTERING ORDER BY (assignmentId asc, timestamp desc, eventId asc)
