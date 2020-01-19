create table devicestate.device_state (
	id uuid not null, created_by varchar(255), 
	created_date timestamp, 
	token varchar(255), 
	updated_by varchar(255), 
	updated_date timestamp, 
	area_id uuid not null, 
	asset_id uuid not null, 
	customer_id uuid not null, 
	device_assignment_id uuid not null, 
	device_id uuid not null, 
	device_type_id uuid not null, 
	last_interaction_date timestamp, 
	presence_missing_date timestamp, 
	primary key (id)
);

create table devicestate.device_state_metadata (
	device_state_id uuid not null, 
	prop_value varchar(255), 
	prop_key varchar(255) not null, 
	primary key (device_state_id, prop_key)
);

alter table if exists devicestate.device_state_metadata 
	add constraint FKch35h87w29d0orr75qoi0qh0d foreign key (device_state_id) references devicestate.device_state;

create table devicestate.recent_state_event (
	id uuid not null, 
	classifier varchar(255), 
	device_state_id uuid, 
	event_date timestamp, 
	event_id uuid, 
	event_type varchar(255), 
	value varchar(255), 
	primary key (id)
);

alter table if exists devicestate.recent_state_event 
	add constraint FKcs807bwapbaep094le50r2uww foreign key (device_state_id) references devicestate.device_state
