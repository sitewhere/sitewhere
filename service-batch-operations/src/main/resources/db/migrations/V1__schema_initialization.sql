create table batchoperations.batch_element (
	id uuid not null, 
	batch_operation_id uuid, 
	device_id uuid, 
	processed_date timestamp, 
	processing_status varchar(255), 
	primary key (id)
);

create table batchoperations.batch_element_metadata (
	batch_element_id uuid not null, 
	prop_value varchar(255), 
	prop_key varchar(255) not null, 
	primary key (batch_element_id, prop_key)
);

create table batchoperations.batch_operation (
	id uuid not null, 
	created_by varchar(255), 
	created_date timestamp, 
	token varchar(255), 
	updated_by varchar(255), 
	updated_date timestamp, 
	operation_type varchar(255), 
	processing_ended_date timestamp, 
	processing_started_date timestamp, 
	processing_status varchar(255), 
	primary key (id)
);

alter table if exists batchoperations.batch_operation 
	add constraint UK_2vwn7f4ddn939hw8cvp71a7sa unique (token);

create table batchoperations.batch_operation_metadata (
	batch_operation_id uuid not null, 
	prop_value varchar(255), 
	prop_key varchar(255) not null, 
	primary key (batch_operation_id, prop_key)
);

alter table if exists batchoperations.batch_element_metadata 
	add constraint FK47b43ar9g7w2qt1w0vpmh3q59 foreign key (batch_element_id) references batchoperations.batch_element;
alter table if exists batchoperations.batch_operation_metadata 
	add constraint FKf0gr47iitj9u5hnnovwxp1eya foreign key (batch_operation_id) references batchoperations.batch_operation;

create table batchoperations.batch_operation_parameters (
	batch_operation_id uuid not null, 
	prop_value varchar(255), 
	prop_key varchar(255) not null, 
	primary key (batch_operation_id, prop_key)
);

alter table if exists batchoperations.batch_operation_parameters 
	add constraint FKo55e5n9plukw4e9du7pnqxjsq foreign key (batch_operation_id) references batchoperations.batch_operation;
	
