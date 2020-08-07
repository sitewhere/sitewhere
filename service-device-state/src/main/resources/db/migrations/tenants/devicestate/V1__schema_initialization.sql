create table devicestate.device_state (
	id uuid not null, 
	device_id uuid not null, 
	device_type_id uuid not null, 
	device_assignment_id uuid not null, 
	customer_id uuid not null, 
	area_id uuid not null, 
	asset_id uuid not null, 
	last_interaction_date timestamp, 
	presence_missing_date timestamp, 
	primary key (id)
);

create index device_state_device on devicestate.device_state (device_id);
create unique index device_state_device_assignment on devicestate.device_state (device_assignment_id);

create table devicestate.device_state_metadata (
	device_state_id uuid not null, 
	prop_value varchar(255), 
	prop_key varchar(255) not null, 
	primary key (device_state_id, prop_key)
);

alter table if exists devicestate.device_state_metadata 
	add constraint FKch35h87w29d0orr75qoi0qh0d foreign key (device_state_id) references devicestate.device_state;
	
create table devicestate.recent_location_event (
	id uuid not null, 
	device_state_id uuid, 
	elevation numeric(12, 2), 
	event_date timestamp, 
	event_id uuid, 
	latitude numeric(12, 8) not null, 
	longitude numeric(12, 8) not null, 
	primary key (id)
);

alter table if exists devicestate.recent_location_event 
	add constraint FKgmxj6ei7ko6aml59g7ts0r1wf foreign key (device_state_id) references devicestate.device_state;

create table devicestate.recent_measurement_event (
	id uuid not null, 
	device_state_id uuid, 
	event_date timestamp, 
	event_id uuid, 
	name varchar(255) not null, 
	value numeric(32, 8) not null, 
	max_value numeric(32, 8) not null, 
	max_value_date timestamp, 
	min_value numeric(32, 8) not null, 
	min_value_date timestamp, 
	primary key (id)
);
	
alter table if exists devicestate.recent_measurement_event 
	add constraint FKmqd0gej8fnlmpq88tec7ouf9r foreign key (device_state_id) references devicestate.device_state;
	
create table devicestate.recent_alert_event (
	id uuid not null, 
	device_state_id uuid, 
	event_date timestamp, 
	event_id uuid not null, 
	level varchar(255) not null, 
	message varchar(255) not null, 
	source varchar(255) not null, 
	type varchar(255) not null, 
	primary key (id)
);

alter table if exists devicestate.recent_alert_event 
	add constraint FKb45d1cyignj5r6dkb3aakhm4n foreign key (device_state_id) references devicestate.device_state;
