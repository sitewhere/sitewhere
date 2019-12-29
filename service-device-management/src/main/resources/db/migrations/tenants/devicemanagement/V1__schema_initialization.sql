create table area
(
    id  uuid not null constraint area_pkey primary key,
    area_type_id      uuid,
    background_color varchar,
    border_color     varchar,
    created_by       varchar,
    created_date     timestamp,
    description     varchar(1000),
    foreground_color varchar,
    icon            varchar,
    image_url        varchar,
    name            varchar,
    parent_id        uuid,
    token           varchar,
    updated_by       varchar,
    updated_date     timestamp
);

create table area_metadata
(
    area_id uuid not null constraint fkhmfvnp287p956qi7bkg5dew6u references area,
    prop_value varchar,
    prop_key   varchar not null,
    constraint area_metadata_pkey primary key (area_id, prop_key)
);

create table area_type
(
    id uuid not null constraint area_type_pkey primary key,
    background_color varchar,
    border_color     varchar,
    created_by       varchar,
    created_date     timestamp,
    description     varchar,
    foreground_color varchar,
    icon            varchar,
    image_url        varchar,
    name            varchar,
    token           varchar,
    updated_by       varchar,
    updated_date     timestamp
);

create table area_type_metadata
(
    area_type_id uuid not null constraint fk63869vnjm3fh73tfou4sr7enb references area_type,
    prop_value   varchar,
    prop_key     varchar not null,
    constraint area_type_metadata_pkey primary key (area_type_id, prop_key)
);

create table contained_area_type_ids
(
    area_type_id uuid not null constraint fkivgj2wi2k2rbhlfao83urn8s0 references area_type,
    contained_area_type_id uuid
);

create table customer
(
    id  uuid not null constraint customer_pkey primary key,
    background_color varchar,
    border_color     varchar,
    created_by       varchar,
    created_date     timestamp,
    customer_type_id uuid,
    description      varchar,
    foreground_color varchar,
    icon             varchar,
    image_url        varchar,
    name             varchar,
    parent_id        uuid,
    token            varchar,
    updated_by       varchar,
    updated_date     timestamp
);

create table customer_metadata
(
    customer_id uuid not null constraint fkegp0a66oeynm3mfdptp0fwdxc references customer,
    prop_value   varchar,
    prop_key     varchar not null,
    constraint customer_metadata_pkey
        primary key (customer_id, prop_key)
);

create table customer_type
(
    id uuid not null constraint customer_type_pkey primary key,
    background_color varchar,
    border_color     varchar,
    created_by       varchar,
    created_date     timestamp,
    description     varchar,
    foreground_color varchar,
    icon            varchar,
    image_url        varchar,
    name            varchar,
    token           varchar,
    updated_by       varchar,
    updated_date     timestamp
);

create table customer_type_metadata
(
    customer_type_id uuid not null constraint fkh73yk0mon5ls1poxue1kwmis3 references customer_type,
    prop_value       varchar,
    prop_key         varchar not null,
    constraint customer_type_metadata_pkey primary key (customer_type_id, prop_key)
);

create table contained_customer_type_ids
(
    customer_type_id uuid not null constraint fk54criwc27w7ln3plk646ana8l references customer_type,
    contained_customer_type_id uuid
);

create table device
(
    id uuid not null constraint device_pkey primary key,
    comments           varchar,
    created_by          varchar,
    created_date        timestamp,
    device_assignment_id uuid,
    device_type_id       uuid,
    device_type_token    varchar,
    parent_device_id     uuid,
    status             varchar,
    token              varchar,
    updated_by          varchar,
    updated_date        timestamp
);

create table device_active_assignment
(
    device_id uuid not null constraint fk3fi15qo5rgptuvrfwasn5dn1l references device,
    active_device_assignment_id uuid
);

create table device_alarm
(
    id  uuid not null constraint device_alarm_pkey primary key,
    acknowledged_date   timestamp,
    alarm_message       varchar,
    area_id             uuid,
    asset_id            uuid,
    customer_id         uuid,
    device_assignment_id uuid,
    device_id           uuid,
    resolved_date       timestamp,
    state              varchar,
    triggered_date      timestamp,
    triggering_event_id  uuid
);

create table device_alarm_metadata
(
    device_alarm_id uuid not null constraint fk7frl1jp0oylh4nkyj4x3g4tjb references device_alarm,
    prop_value      varchar,
    prop_key        varchar not null,
    constraint device_alarm_metadata_pkey primary key (device_alarm_id, prop_key)
);

create table device_element_schema
(
    id   uuid not null constraint device_element_schema_pkey primary key,
    name varchar,
    path varchar
);

create table device_unit
(
    id   uuid not null
        constraint device_util_pkey primary key,
    name varchar,
    path varchar,
    id_schema uuid constraint device_unit_element_schema_fk references device_element_schema,
    parent_id uuid constraint device_unit_parent_fk references device_unit
);

create table device_slot
(
    id uuid not null constraint device_slot_pkey primary key,
    name varchar,
    path varchar,
    device_unit_id uuid constraint device_unit_fk references device_unit(id),
    device_element_schema_id uuid constraint device_element_schema_fk references device_element_schema(id)
);

create table device_assignment
(
    id uuid not null constraint device_assignment_pkey primary key,
    active_date   timestamp,
    area_id       uuid,
    asset_id      uuid,
    created_by    varchar,
    created_date  timestamp,
    customer_id   uuid,
    device_id     uuid,
    device_type_id uuid,
    released_date timestamp,
    status       varchar,
    token        varchar,
    updated_by    varchar,
    updated_date  timestamp
);

create table device_assignment_metadata
(
    device_assignment_id uuid not null constraint fkj09gyp8kafgn9m9e5oeqwdqms references device_assignment,
    prop_value varchar,
    prop_key   varchar not null,
    constraint device_assignment_metadata_pkey primary key (device_assignment_id, prop_key)
);

create table device_command
(
    id uuid not null constraint device_command_pkey primary key,
    created_by       varchar,
    created_date     timestamp,
    description     varchar,
    device_type_id    uuid,
    device_type_token varchar,
    name            varchar,
    namespace       varchar,
    token           varchar unique,
    updated_by      varchar,
    updated_date    timestamp
);

create table device_command_metadata
(
    device_command_id uuid not null constraint fkhndjvpa5li7al6pwcne8jmq1p references device_command,
    prop_value        varchar,
    prop_key          varchar not null,
    constraint device_command_metadata_pkey primary key (device_command_id, prop_key)
);


create table command_parameter
(
    id uuid not null constraint command_parameter_pkey primary key,
    name     varchar,
    required boolean not null,
    type     varchar,
    device_command_id uuid not null references device_command (id)
);

create table device_element_mapping
(
    id uuid not null constraint device_element_mapping_pkey primary key,
    device_element_schema_path varchar,
    device_token varchar,
    device_id uuid not null references device (id)
);

create table device_group
(
    id  uuid not null constraint device_group_pkey primary key,
    background_color varchar,
    border_color     varchar,
    created_by       varchar,
    created_date     timestamp,
    description     varchar,
    foreground_color varchar,
    icon            varchar,
    image_url        varchar,
    name            varchar,
    token           varchar,
    updated_by       varchar,
    updated_date     timestamp
);

create table device_group_element
(
    id uuid not null constraint device_group_element_pkey primary key,
    device_id      uuid,
    group_id       uuid,
    nested_group_id uuid
);

create table device_group_metadata
(
    device_group_id uuid not null constraint fkagh6eu89prqlut8wgrd7ru104 references device_group,
    prop_value      varchar,
    prop_key        varchar not null,
    constraint device_group_metadata_pkey primary key (device_group_id, prop_key)
);

create table device_metadata
(
    device_id uuid not null constraint fklvnhinwxcopir2afw4hhd21dv references device,
    prop_value varchar,
    prop_key   varchar not null,
    constraint device_metadata_pkey
        primary key (device_id, prop_key)
);

create table device_status
(
    id uuid not null constraint device_status_pkey primary key,
    background_color varchar,
    border_color     varchar,
    code            varchar,
    created_by       varchar,
    created_date     timestamp,
    device_type_id    uuid,
    device_type_token varchar,
    foreground_color varchar,
    icon            varchar,
    name            varchar,
    token           varchar,
    updated_by       varchar,
    updated_date     timestamp
);

create table device_status_metadata
(
    device_status_id uuid not null constraint fk6hgkc6l5eh6j8d31tg2r1jb5g references device_status,
    prop_value       varchar,
    prop_key         varchar not null,
    constraint device_status_metadata_pkey primary key (device_status_id, prop_key)
);

create table device_type
(
    id uuid not null constraint device_type_pkey primary key,
    background_color     varchar,
    border_color         varchar,
    container_policy     varchar,
    created_by           varchar,
    created_date         timestamp,
    description         varchar(1000),
    device_element_schema_id uuid,
    foreground_color     varchar,
    icon                varchar,
    image_url            varchar,
    name                varchar,
    token               varchar,
    updated_by           varchar,
    updated_date         timestamp
);

create table device_type_metadata
(
    device_type_id uuid not null constraint fk2l7wclaxgtbmwulpn4doah587 references device_type,
    prop_value     varchar,
    prop_key       varchar not null,
    constraint device_type_metadata_pkey primary key (device_type_id, prop_key)
);

create table device_group_roles
(
    device_group_id uuid not null constraint fkautwqvnlkybd7neyphvpmvsv7 references device_group,
    role  varchar
);

create table device_group_element_roles
(
    device_group_element_id uuid not null constraint fkrhhirutvofaadogo9i22n6wm references device_group_element,
    role varchar
);

create table zone
(
    id uuid not null constraint zone_pkey primary key,
    area_id      uuid,
    border_color varchar,
    created_by   varchar,
    created_date timestamp,
    fill_color   varchar,
    name        varchar,
    border_opacity     double precision,
    fill_opacity     double precision,
    token       varchar,
    updated_by   varchar,
    updated_date timestamp
);

create table location
(
    id uuid not null constraint location_pkey primary key,
    elevation double precision,
    latitude  double precision,
    longitude double precision,
    area_id   uuid references area(id),
    zone_id   uuid references zone(id)
);

create table zone_metadata
(
    zone_id uuid not null constraint fkemse0xhpumauen49j0jnllp21 references zone,
    prop_value varchar,
    prop_key   varchar not null,
    constraint zone_metadata_pkey
        primary key (zone_id, prop_key)
);
