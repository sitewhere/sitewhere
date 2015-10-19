Schedules
=========
Schedules provide the ability to run jobs at another point in time
rather than immediately firing an action. All schedules may be bounded
by a start and end time during which the firing of the job is 
controlled by other scheduling criteria. Simple scheduling allows
for a job to be fired at an interval and repeated for a given number
of times (or repeated infinitely if desired). Alternatively, jobs
may be specified as cron expressions that indicate a specific schedule
on which they run (e.g. every Monday, Wednesday, and Friday at 2pm and
8pm except for holidays).