def schedule;

// ################ //
// Create Schedules //
// ################ //
schedule = scheduleBuilder.newSchedule '95ff6a81-3d92-4b10-b8af-957c172ad97b', 'Every thirty seconds' withSimpleSchedule(30 * 1000, 0)
scheduleBuilder.persist schedule

schedule = scheduleBuilder.newSchedule '20f5e855-d8a2-431d-a68b-61f4549dbb80', 'Every minute' withSimpleSchedule(60 * 1000, 0)
scheduleBuilder.persist schedule

schedule = scheduleBuilder.newSchedule '2c82d6d5-6a0a-48be-99ab-7451a69e3ba7', 'Every 10 minutes' withSimpleSchedule(10 * 60 * 1000, 0)
scheduleBuilder.persist schedule

schedule = scheduleBuilder.newSchedule 'ee23196c-5bc3-4685-8c9d-6dcbb3062ec2', 'On the half hour' withCronSchedule '0 0/30 * 1/1 * ? *'
scheduleBuilder.persist schedule

schedule = scheduleBuilder.newSchedule 'de305d54-75b4-431b-adb2-eb6b9e546014', 'On the hour' withCronSchedule '0 0 0/1 1/1 * ? *'
scheduleBuilder.persist schedule
