create table devicemanagement.area (
	id uuid not null, 
	created_by varchar(255), 
	created_date timestamp, 
	token varchar(255), 
	updated_by varchar(255), 
	updated_date timestamp, 
	background_color varchar(255), 
	border_color varchar(255), 
	foreground_color varchar(255), 
	icon varchar(255), 
	image_url varchar(255), 
	area_type_id uuid, 
	description varchar(1024), 
	name varchar(255), 
	parent_id uuid, 
	primary key (id)
);

alter table if exists devicemanagement.area 
	add constraint UKrudrrgb0fm0h55dg080db0tsb unique (token);
alter table if exists devicemanagement.area 
	add constraint FK2dmtr5518yrmeswf3hau5ksik foreign key (parent_id) references devicemanagement.area;
	
create table devicemanagement.area_boundary (
	id uuid not null, 
	elevation float8, 
	latitude float8, 
	longitude float8, 
	area_id uuid not null, 
	primary key (id)
);

alter table if exists devicemanagement.area_boundary 
	add constraint FK7e7eg6saqp6hu1d2j2aue7sri foreign key (area_id) references devicemanagement.area;

create table devicemanagement.area_metadata (
	area_id uuid not null, 
	prop_value varchar(255), 
	prop_key varchar(255) not null, 
	primary key (area_id, prop_key)
);

alter table if exists devicemanagement.area_metadata 
	add constraint FKdtewccsec602oh7oorq2u2bv0 foreign key (area_id) references devicemanagement.area;

create table devicemanagement.area_type (
	id uuid not null, 
	created_by varchar(255), 
	created_date timestamp, 
	token varchar(255), 
	updated_by varchar(255), 
	updated_date timestamp, 
	background_color varchar(255), 
	border_color varchar(255), 
	foreground_color varchar(255), 
	icon varchar(255), image_url varchar(255), 
	description varchar(1024), 
	name varchar(255), 
	primary key (id)
);

alter table if exists devicemanagement.area_type 
	add constraint UKebow8w7gya5xo9tv0st5di3t8 unique (token);
alter table if exists devicemanagement.area 
	add constraint FKnthup5wgq7m7rk8dkpoewbuyv foreign key (area_type_id) references devicemanagement.area_type;

create table devicemanagement.area_type_metadata (
	area_type_id uuid not null, 
	prop_value varchar(255), 
	prop_key varchar(255) not null, 
	primary key (area_type_id, prop_key)
);

alter table if exists devicemanagement.area_type_metadata 
	add constraint FKmow76lp4asveec1sb9c0ajjso foreign key (area_type_id) references devicemanagement.area_type;

create table devicemanagement.command_parameter (
	id uuid not null, 
	name varchar(255), 
	required boolean, 
	type varchar(255), 
	device_command_id uuid not null, 
	primary key (id)
);

create table devicemanagement.contained_area_types (
	parent_area_type_id uuid not null, 
	child_area_type_id uuid not null
);

alter table if exists devicemanagement.contained_area_types 
	add constraint FKq80500k6dpp3rwu62tru6tcwc foreign key (child_area_type_id) references devicemanagement.area_type;
alter table if exists devicemanagement.contained_area_types 
	add constraint FK3598kxugvflfwd3u0plpucc4h foreign key (parent_area_type_id) references devicemanagement.area_type;

create table devicemanagement.contained_customer_types (
	parent_customer_type_id uuid not null, 
	child_customer_type_id uuid not null
);

create table devicemanagement.customer (
	id uuid not null, created_by varchar(255), 
	created_date timestamp, token varchar(255), 
	updated_by varchar(255), 
	updated_date timestamp, 
	background_color varchar(255), 
	border_color varchar(255), 
	foreground_color varchar(255), 
	icon varchar(255), 
	image_url varchar(255), 
	customer_type_id uuid, 
	description varchar(1024), 
	name varchar(255), 
	parent_id uuid, 
	primary key (id)
);

alter table if exists devicemanagement.customer 
	add constraint UKi1p08swb0onyuxes5tjx73rqg unique (token);
alter table if exists devicemanagement.customer 
	add constraint FK70184a6yqo1yjbbi9x18mvftc foreign key (parent_id) references devicemanagement.customer;

create table devicemanagement.customer_metadata (
	customer_type_id uuid not null, 
	prop_value varchar(255), 
	prop_key varchar(255) not null, 
	primary key (customer_type_id, prop_key)
);

alter table if exists devicemanagement.customer_metadata 
	add constraint FKmc2l27xdnvr9ku9qub7ok5no5 foreign key (customer_type_id) references devicemanagement.customer;

create table devicemanagement.customer_type (
	id uuid not null, 
	created_by varchar(255), 
	created_date timestamp, 
	token varchar(255), 
	updated_by varchar(255), 
	updated_date timestamp, 
	background_color varchar(255), 
	border_color varchar(255), 
	foreground_color varchar(255), 
	icon varchar(255), 
	image_url varchar(255), 
	description varchar(1024), 
	name varchar(255), 
	primary key (id)
);

alter table if exists devicemanagement.customer_type 
	add constraint UK584b1s9rv7yquuttuu5bx0puk unique (token);
alter table if exists devicemanagement.customer 
	add constraint FKn8vf9jf3m29plqn6rx45p2pl7 foreign key (customer_type_id) references devicemanagement.customer_type;
alter table if exists devicemanagement.contained_customer_types 
	add constraint FKbx6e5afmk5b5c44q46aw7hdrf foreign key (child_customer_type_id) references devicemanagement.customer_type;
alter table if exists devicemanagement.contained_customer_types 
	add constraint FKorsqr5f1eyiycot107yw2sk7f foreign key (parent_customer_type_id) references devicemanagement.customer_type;

create table devicemanagement.customer_type_metadata (
	customer_type_id uuid not null, 
	prop_value varchar(255), 
	prop_key varchar(255) not null, 
	primary key (customer_type_id, prop_key)
);

alter table if exists devicemanagement.customer_type_metadata 
	add constraint FK4i255eelfy9h56oo2apjkww1b foreign key (customer_type_id) references devicemanagement.customer_type;

create table devicemanagement.device (
	id uuid not null, 
	created_by varchar(255), 
	created_date timestamp, 
	token varchar(255), 
	updated_by varchar(255), 
	updated_date timestamp, 
	comments varchar(1024), 
	device_type_id uuid not null, 
	parent_device_id uuid, 
	status varchar(255), 
	primary key (id)
);

alter table if exists devicemanagement.device 
	add constraint UKn1ybpxp03vbsysx83ex0dj6xl unique (token);
alter table if exists devicemanagement.device 
	add constraint FK407b76lvky0edm9leklkxf0u6 foreign key (parent_device_id) references devicemanagement.device;

create table devicemanagement.device_alarm (
	id uuid not null, 
	acknowledged_date timestamp, 
	alarm_message varchar(1024), 
	area_id uuid, asset_id uuid, 
	customer_id uuid, 
	device_assignment_id uuid, 
	device_id uuid, 
	resolved_date timestamp, 
	state varchar(255), 
	triggered_date timestamp, 
	triggering_event_id uuid, 
	primary key (id)
);

alter table if exists devicemanagement.device_alarm 
	add constraint FKqua8scu5ea5sjede4gr0828g3 foreign key (area_id) references devicemanagement.area;
alter table if exists devicemanagement.device_alarm 
	add constraint FKi4ewckrslpj0mhr707pp24gg foreign key (customer_id) references devicemanagement.customer;
alter table if exists devicemanagement.device_alarm 
	add constraint FKpmfs22x1cg2hwhl2n250kmwao foreign key (device_id) references devicemanagement.device;

create table devicemanagement.device_alarm_metadata (
	device_alarm_id uuid not null, 
	prop_value varchar(255), 
	prop_key varchar(255) not null, 
	primary key (device_alarm_id, prop_key)
);

alter table if exists devicemanagement.device_alarm_metadata 
	add constraint FKqrioneeqpjtoklfx2v9myby6l foreign key (device_alarm_id) references devicemanagement.device_alarm;

create table devicemanagement.device_assignment (
	id uuid not null, 
	created_by varchar(255), 
	created_date timestamp, 
	token varchar(255), 
	updated_by varchar(255), 
	updated_date timestamp, 
	active_date timestamp, 
	area_id uuid, 
	asset_id uuid, 
	customer_id uuid, 
	device_id uuid, 
	device_type_id uuid, 
	released_date timestamp, 
	status varchar(255), 
	primary key (id)
);

alter table if exists devicemanagement.device_assignment 
	add constraint UKgj18sxfpuna7qjnlpwl2h3lkn unique (token);
alter table if exists devicemanagement.device_assignment 
	add constraint FK2jqk0lrnqbdkqrdkyqlp2ibf7 foreign key (device_id) references devicemanagement.device;
alter table if exists devicemanagement.device_assignment 
	add constraint FK43mo11770ycicj2e6xst18q76 foreign key (area_id) references devicemanagement.area;
alter table if exists devicemanagement.device_assignment 
	add constraint FK4kinev2orhiswlpxjhb0i92aw foreign key (customer_id) references devicemanagement.customer;
alter table if exists devicemanagement.device_alarm 
	add constraint FK83m49a8sgnd0il6jev29k3y9l foreign key (device_assignment_id) references devicemanagement.device_assignment;

create table devicemanagement.device_assignment_metadata (
	device_assignment_id uuid not null, 
	prop_value varchar(255), 
	prop_key varchar(255) not null, 
	primary key (device_assignment_id, prop_key)
);

alter table if exists devicemanagement.device_assignment_metadata 
	add constraint FK324eruvhibh708kvmduk9ecfa foreign key (device_assignment_id) references devicemanagement.device_assignment;

create table devicemanagement.device_command (
	id uuid not null, 
	created_by varchar(255), 
	created_date timestamp, 
	token varchar(255), 
	updated_by varchar(255), 
	updated_date timestamp, 
	description varchar(1024), 
	device_type_id uuid, 
	name varchar(255), 
	namespace varchar(255), 
	primary key (id)
);

alter table if exists devicemanagement.device_command 
	add constraint UK7o1v1vlo4n9r7trps66yorv20 unique (token);
alter table if exists devicemanagement.command_parameter 
	add constraint FK545gd0i40baxsq153ntqnhrub foreign key (device_command_id) references devicemanagement.device_command;

create table devicemanagement.device_command_metadata (
	device_command_id uuid not null, 
	prop_value varchar(255), 
	prop_key varchar(255) not null, 
	primary key (device_command_id, prop_key)
);

alter table if exists devicemanagement.device_command_metadata 
	add constraint FK6mxoujmskh3u1viyu19n8m232 foreign key (device_command_id) references devicemanagement.device_command;

create table devicemanagement.device_element_mapping (
	id uuid not null, 
	device_element_schema_path varchar(255), 
	device_token varchar(255), 
	device_id uuid not null, 
	primary key (id)
);

alter table if exists devicemanagement.device_element_mapping 
	add constraint FKqsacr6f6u2sas4327808f6wjv foreign key (device_id) references devicemanagement.device;

create table devicemanagement.device_element_schema (
	id uuid not null, 
	created_by varchar(255), 
	created_date timestamp, 
	token varchar(255), 
	updated_by varchar(255), 
	updated_date timestamp, 
	name varchar(255), 
	path varchar(255), 
	primary key (id)
);

alter table if exists devicemanagement.device_element_schema 
	add constraint UKr1ew8sprwxdg7o2aonjvjx20p unique (token);

create table devicemanagement.device_element_schema_metadata (
	device_element_schema_id uuid not null, 
	prop_value varchar(255), 
	prop_key varchar(255) not null, 
	primary key (device_element_schema_id, prop_key)
);

alter table if exists devicemanagement.device_element_schema_metadata 
	add constraint FK98g45r3hh4k6viqjomjjsne7r foreign key (device_element_schema_id) references devicemanagement.device_element_schema;

create table devicemanagement.device_group (
	id uuid not null, created_by varchar(255), 
	created_date timestamp, 
	token varchar(255), 
	updated_by varchar(255), 
	updated_date timestamp, 
	background_color varchar(255), 
	border_color varchar(255), 
	foreground_color varchar(255), 
	icon varchar(255), 
	image_url varchar(255), 
	description varchar(1024), 
	name varchar(255), 
	primary key (id)
);

alter table if exists devicemanagement.device_group 
	add constraint UK87pdlj3liy1i32bh5idtgveef unique (token);

create table devicemanagement.device_group_element (
	id uuid not null, 
	created_by varchar(255), 
	created_date timestamp, 
	token varchar(255), 
	updated_by varchar(255), 
	updated_date timestamp, 
	device_id uuid, 
	group_id uuid not null, 
	nested_group_id uuid, 
	primary key (id)
);

alter table if exists devicemanagement.device_group_element 
	add constraint FK6cta8dujfg17j8qdjcd0rvscq foreign key (device_id) references devicemanagement.device;
alter table if exists devicemanagement.device_group_element 
	add constraint FKdlvslctgh3axt3r4t17jlraep foreign key (group_id) references devicemanagement.device_group;
alter table if exists devicemanagement.device_group_element 
	add constraint FKgi6rkig707nqkm49v5rsvxixu foreign key (nested_group_id) references devicemanagement.device_group;

create table devicemanagement.device_group_element_roles (
	device_group_element_id uuid not null, 
	role varchar(255)
);

alter table if exists devicemanagement.device_group_element_roles 
	add constraint FKn2mvogw0gomknvurg09n18jel foreign key (device_group_element_id) references devicemanagement.device_group_element;

create table devicemanagement.device_group_element_metadata (
	device_group_element_id uuid not null, 
	prop_value varchar(255), 
	prop_key varchar(255) not null, 
	primary key (device_group_element_id, prop_key)
);

alter table if exists devicemanagement.device_group_element_metadata 
	add constraint FKc2ydrlermch5a33986iuycahm foreign key (device_group_element_id) references devicemanagement.device_group_element;

create table devicemanagement.device_group_metadata (
	device_group_id uuid not null, 
	prop_value varchar(255), 
	prop_key varchar(255) not null, 
	primary key (device_group_id, prop_key)
);

alter table if exists devicemanagement.device_group_metadata 
	add constraint FKkd3dexbifo2k9q3uacdbliubr foreign key (device_group_id) references devicemanagement.device_group;

create table devicemanagement.device_group_roles (
	device_group_id uuid not null, 
	role varchar(255)
);

alter table if exists devicemanagement.device_group_roles 
	add constraint FKhf6i5ur6tj1ads8sd8i64u8bn foreign key (device_group_id) references devicemanagement.device_group;

create table devicemanagement.device_metadata (
	device_id uuid not null, 
	prop_value varchar(255), 
	prop_key varchar(255) not null, 
	primary key (device_id, prop_key)
);

alter table if exists devicemanagement.device_metadata 
	add constraint FKmb3ls1fsa08c84w1xls93whq4 foreign key (device_id) references devicemanagement.device;

create table devicemanagement.device_slot (
	id uuid not null, 
	created_by varchar(255), 
	created_date timestamp, 
	token varchar(255), 
	updated_by varchar(255), 
	updated_date timestamp, 
	name varchar(255), 
	path varchar(255), 
	device_element_schema_id uuid, 
	device_unit_id uuid, 
	primary key (id)
);

alter table if exists devicemanagement.device_slot 
	add constraint UKlf3syhy3acwjoica1jibcxxd3 unique (token);
alter table if exists devicemanagement.device_slot 
	add constraint FK9qcpg8r7rvlj3us0wh5e86489 foreign key (device_element_schema_id) references devicemanagement.device_element_schema;

create table devicemanagement.device_slot_metadata (
	device_slot_id uuid not null, 
	prop_value varchar(255), 
	prop_key varchar(255) not null, 
	primary key (device_slot_id, prop_key)
);

alter table if exists devicemanagement.device_slot_metadata 
	add constraint FK1n5x6lu8qhmp5rexda362n37l foreign key (device_slot_id) references devicemanagement.device_slot;

create table devicemanagement.device_status (
	id uuid not null, 
	created_by varchar(255), 
	created_date timestamp, 
	token varchar(255), 
	updated_by varchar(255), 
	updated_date timestamp, 
	background_color varchar(255), 
	border_color varchar(255), 
	code varchar(255), 
	device_type_id uuid, 
	foreground_color varchar(255), 
	icon varchar(255), 
	name varchar(255), 
	primary key (id)
);

alter table if exists devicemanagement.device_status 
	add constraint UKod5de1uet0qwq9lbpndti2ph2 unique (token);

create table devicemanagement.device_status_metadata (
	device_status_id uuid not null, 
	prop_value varchar(255), 
	prop_key varchar(255) not null, 
	primary key (device_status_id, prop_key)
);

alter table if exists devicemanagement.device_status_metadata 
	add constraint FKhlyajavfacu5kavagi4ndjx54 foreign key (device_status_id) references devicemanagement.device_status;

create table devicemanagement.device_type (
	id uuid not null, 
	created_by varchar(255), 
	created_date timestamp, 
	token varchar(255), 
	updated_by varchar(255), 
	updated_date timestamp, 
	background_color varchar(255), 
	border_color varchar(255), 
	foreground_color varchar(255), 
	icon varchar(255), 
	image_url varchar(255), 
	container_policy varchar(255), 
	description varchar(1024), 
	device_element_schema_id uuid, 
	name varchar(255), 
	primary key (id)
);

alter table if exists devicemanagement.device_type 
	add constraint UKt560kejuxj38tdgu7vpd7pjd3 unique (token);
alter table if exists devicemanagement.device_type 
	add constraint FKt81gytf4cc97fyhdwl64c0tin foreign key (device_element_schema_id) references devicemanagement.device_element_schema;
alter table if exists devicemanagement.device 
	add constraint FKo9oabhnk3f79y77ifapu6yp7t foreign key (device_type_id) references devicemanagement.device_type;
alter table if exists devicemanagement.device_assignment 
	add constraint FKjit064bbrpbd1xsvh0lyoyl30 foreign key (device_type_id) references devicemanagement.device_type;
alter table if exists devicemanagement.device_command 
	add constraint FKtgp3vk1kdgr3m6hc02lgdb1qf foreign key (device_type_id) references devicemanagement.device_type;

create table devicemanagement.device_type_metadata (
	device_type_id uuid not null, 
	prop_value varchar(255), 
	prop_key varchar(255) not null, 
	primary key (device_type_id, prop_key)
);

alter table if exists devicemanagement.device_type_metadata 
	add constraint FK6hytdgl1nm7j4j723rhydc4kh foreign key (device_type_id) references devicemanagement.device_type;

create table devicemanagement.device_unit (
	id uuid not null, 
	created_by varchar(255), 
	created_date timestamp, 
	token varchar(255), 
	updated_by varchar(255), 
	updated_date timestamp, 
	name varchar(255), 
	path varchar(255), 
	parent_id uuid, 
	parentId uuid, 
	primary key (id)
);

alter table if exists devicemanagement.device_unit 
	add constraint UKluuwl77ih7apqm9o04gwcdnvc unique (token);
alter table if exists devicemanagement.device_unit 
	add constraint FKfwbctd10qtda4iqe38p5hlpa4 foreign key (parent_id) references devicemanagement.device;
alter table if exists devicemanagement.device_unit 
	add constraint FK8ca41s2auhtibgfr5psv0qiyf foreign key (parentId) references devicemanagement.device_unit;
alter table if exists devicemanagement.device_slot 
	add constraint FKt2ic99iofu5lmnq8k6hfqss41 foreign key (device_unit_id) references devicemanagement.device_unit;

create table devicemanagement.device_unit_metadata (
	device_unit_id uuid not null, 
	prop_value varchar(255), 
	prop_key varchar(255) not null, 
	primary key (device_unit_id, prop_key)
);

alter table if exists devicemanagement.device_unit_metadata 
	add constraint FK6nuw4i8qyn5jmyap2i5vbcb6k foreign key (device_unit_id) references devicemanagement.device_unit;

create table devicemanagement.location (
	id uuid not null, 
	elevation float8, 
	latitude float8, 
	longitude float8, 
	area_id uuid, 
	zone_id uuid, 
	primary key (id)
);

alter table if exists devicemanagement.location 
	add constraint FKkx17wmdxmy67gc9t04pghuwdp foreign key (area_id) references devicemanagement.area;

create table devicemanagement.zone (
	id uuid not null, 
	created_by varchar(255), 
	created_date timestamp, 
	token varchar(255), 
	updated_by varchar(255), 
	updated_date timestamp, 
	area_id uuid, 
	border_color varchar(255), 
	border_opacity float8, 
	fill_color varchar(255), 
	fill_opacity float8, 
	name varchar(255), 
	primary key (id)
);

alter table if exists devicemanagement.zone 
	add constraint UKi2v17tnrc10ri0oxuvv8prlri unique (token);
alter table if exists devicemanagement.location 
	add constraint FKairbravxei9ggr7lysb7c6bu0 foreign key (zone_id) references devicemanagement.zone;

create table devicemanagement.zone_metadata (
	zone_id uuid not null, 
	prop_value varchar(255), 
	prop_key varchar(255) not null, 
	primary key (zone_id, prop_key)
);

alter table if exists devicemanagement.zone_metadata 
	add constraint FKkvm0orjd7knnx3d8jwn0cp6ai foreign key (zone_id) references devicemanagement.zone;

create table devicemanagement.zone_boundary (
	id uuid not null, 
	elevation float8, 
	latitude float8, 
	longitude float8, 
	zone_id uuid not null, 
	primary key (id)
);

alter table if exists devicemanagement.zone_boundary 
	add constraint FK2cji29qkja2p16alve5q1qom7 foreign key (zone_id) references devicemanagement.zone;

create view devicemanagement.device_summary as
	select dev.*,
		devt.name as device_type_name, devt.image_url as device_type_image_url
			from devicemanagement.device dev
			left join devicemanagement.device_type devt on dev.device_type_id = devt.id;

create view devicemanagement.device_assignment_summary as
	select assn.*,
		dev.token as device_token,
		devt.name as device_type_name, devt.image_url as device_type_image_url,
		area.name as area_name, area.image_url as area_image_url,
		cust.name as customer_name, cust.image_url as customer_image_url,
		null as asset_name, null as asset_image_url
			from devicemanagement.device_assignment assn 
			join devicemanagement.device dev on assn.device_id = dev.id
			join devicemanagement.device_type devt on assn.device_type_id = devt.id
			left join devicemanagement.customer cust on assn.customer_id = cust.id
			left join devicemanagement.area area on assn.area_id = area.id;
	