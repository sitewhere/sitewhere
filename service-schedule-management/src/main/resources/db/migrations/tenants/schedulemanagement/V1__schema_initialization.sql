create table schedulemanagement.schedule (
	id uuid not null, 
	created_by varchar(255), 
	created_date timestamp, 
	token varchar(255), 
	updated_by varchar(255), 
	updated_date timestamp, 
	end_date timestamp, 
	name varchar(255), 
	start_date timestamp, 
	trigger_type varchar(255), 
	primary key (id)
);

alter table if exists schedulemanagement.schedule 
	add constraint UK_3a3gou440pi5u4l0xydlcl5yh unique (token);

create table schedulemanagement.schedule_metadata (
	schedule_id uuid not null, 
	prop_value varchar(255), 
	prop_key varchar(255) not null, 
	primary key (schedule_id, prop_key)
);

alter table if exists schedulemanagement.schedule_metadata 
	add constraint FKt5odkir1ju9qcri8pm9odx38e foreign key (schedule_id) references schedulemanagement.schedule;

create table schedulemanagement.scheduled_job (
	id uuid not null, 
	created_by varchar(255), 
	created_date timestamp, 
	token varchar(255), 
	updated_by varchar(255), 
	updated_date timestamp, 
	job_state varchar(255), 
	job_type varchar(255), 
	schedule_id uuid not null, 
	primary key (id)
);

alter table if exists schedulemanagement.scheduled_job 
	add constraint UK_4u1cm087l6n50jrd0yeqkq9sl unique (token);

create table schedulemanagement.scheduled_job_configuration (
	scheduled_job_id uuid not null, 
	prop_value varchar(255), 
	prop_key varchar(255) not null, 
	primary key (scheduled_job_id, prop_key)
);

alter table if exists schedulemanagement.scheduled_job_configuration 
	add constraint FKexllgkhy9eajt73rb133sv2b3 foreign key (scheduled_job_id) references schedulemanagement.scheduled_job;

create table schedulemanagement.scheduled_job_metadata (
	scheduled_job_id uuid not null, 
	prop_value varchar(255), 
	prop_key varchar(255) not null, 
	primary key (scheduled_job_id, prop_key)
);

alter table if exists schedulemanagement.scheduled_job_metadata 
	add constraint FK1blk0y8xkvnx69o2iv1yg93x1 foreign key (scheduled_job_id) references schedulemanagement.scheduled_job;

create table schedulemanagement.trigger_configuration (
	schedule_id uuid not null, 
	prop_value varchar(255), 
	prop_key varchar(255) not null, 
	primary key (schedule_id, prop_key)
);

alter table if exists schedulemanagement.trigger_configuration 
	add constraint FKinnn7t8i1adwju7ew05xlenhb foreign key (schedule_id) references schedulemanagement.schedule;
