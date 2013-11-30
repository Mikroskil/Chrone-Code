-- RESET

DROP TABLE IF EXISTS attendance, event, membership, organization, ticket, "user";

-- STRUCTURE

CREATE TABLE organization
(
  id serial NOT NULL,
  username character varying(100) NOT NULL,
  password character varying(100) NOT NULL,
  name character varying(100) NOT NULL,
  email character varying(100),
  photo character varying(255),
  about character varying(255),
  created_at timestamp with time zone NOT NULL,
  member_count integer NOT NULL DEFAULT 0,
  CONSTRAINT organization_pkey PRIMARY KEY (id ),
  CONSTRAINT organization_username_key UNIQUE (username )
);

CREATE TABLE "user"
(
  id serial NOT NULL,
  username character varying(100) NOT NULL,
  password character varying(100) NOT NULL,
  name character varying(100) NOT NULL,
  email character varying(100),
  photo character varying(255),
  about character varying(255),
  phone character varying(20),
  created_at timestamp with time zone NOT NULL,
  CONSTRAINT user_pkey PRIMARY KEY (id ),
  CONSTRAINT user_username_key UNIQUE (username )
);

CREATE TABLE membership
(
  org_id serial NOT NULL,
  user_id serial NOT NULL,
  approved boolean NOT NULL DEFAULT false,
  join_at timestamp with time zone NOT NULL,
  qrcode character varying(255),
  CONSTRAINT membership_pkey PRIMARY KEY (org_id , user_id ),
  CONSTRAINT membership_org_id_fkey FOREIGN KEY (org_id)
      REFERENCES organization (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT membership_user_id_fkey FOREIGN KEY (user_id)
      REFERENCES "user" (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE TABLE event
(
  id serial NOT NULL,
  title character varying(255) NOT NULL,
  photo character varying(255),
  org_id serial NOT NULL,
  created_at timestamp with time zone NOT NULL,
  start_at timestamp with time zone,
  end_at timestamp with time zone,
  location character varying(255),
  "desc" character varying(255),
  public boolean NOT NULL DEFAULT false,
  ticket_count integer NOT NULL DEFAULT 0,
  CONSTRAINT event_pkey PRIMARY KEY (id ),
  CONSTRAINT event_org_id_fkey FOREIGN KEY (org_id)
      REFERENCES organization (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE TABLE ticket
(
  user_id serial NOT NULL,
  event_id serial NOT NULL,
  id serial NOT NULL,
  CONSTRAINT ticket_pkey PRIMARY KEY (id ),
  CONSTRAINT ticket_event_id_fkey FOREIGN KEY (event_id)
      REFERENCES event (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT ticket_user_id_fkey FOREIGN KEY (user_id)
      REFERENCES "user" (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE TABLE attendance
(
  id serial NOT NULL,
  ticket_id serial NOT NULL,
  verify_at timestamp with time zone NOT NULL,
  CONSTRAINT attendance_pkey PRIMARY KEY (id ),
  CONSTRAINT attendance_ticket_id_fkey FOREIGN KEY (ticket_id)
      REFERENCES ticket (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);

-- DATA

INSERT INTO organization (username, password, name, email, created_at) VALUES
('mpc', 'mpc', 'Mikroskil Programming Club', 'mikroskilprogrammingclub@gmail.com', now());

INSERT INTO "user" (username, password, name, created_at) VALUES
('erwin', 'erwin', 'Erwin', now()),
('alpintaisei', 'alpintaisei', 'Alpin Taisei', now()),
('herrygozali', 'herrygozali', 'Herry Gozali', now());

INSERT INTO membership (org_id, user_id, approved, join_at) VALUES
(1, 1, true, now()),
(1, 2, true, now()),
(1, 3, false, now());
UPDATE organization SET member_count=3 where id=1;

INSERT INTO event (title, org_id, created_at) VALUES
('Basecamp & Hackathon', 1, now()),
('Algorithm Basic Class', 1, now()),
('Algorithm Intermediate Class', 1, now()),
('TechSpeak: Startup', 1, now());

INSERT INTO ticket (user_id, event_id) VALUES
(1, 1),
(1, 2),
(1, 3),
(1, 4),
(2, 1),
(2, 2),
(2, 4),
(3, 1),
(3, 4);
UPDATE event SET ticket_count=3 where id=1;
UPDATE event SET ticket_count=2 where id=2;
UPDATE event SET ticket_count=1 where id=3;
UPDATE event SET ticket_count=3 where id=4;

INSERT INTO attendance (ticket_id, verify_at) VALUES
(1, now()),
(2, now()),
(3, now()),
(4, now()),
(5, now()),
(6, now()),
(7, now()),
(8, now());
