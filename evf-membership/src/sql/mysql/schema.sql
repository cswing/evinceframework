CREATE TABLE evf_membership_user(
        id						INT				NOT NULL AUTO_INCREMENT,
        oid                     VARCHAR(36)     NOT NULL,
        version                 INT				NOT NULL DEFAULT 0,
        displayName				NVARCHAR(64)	NOT NULL,
		emailAddress			NVARCHAR(254)	NOT NULL,
		password				NVARCHAR(256)	NULL,
		account_expiration_dt	DATETIME		NULL,
		password_expiration_dt	DATETIME		NULL,
		failed_authentication_attempts INT		NOT NULL DEFAULT 0,
		last_authentication_dt	DATETIME		NULL,
		last_failed_authentication_dt DATETIME	NULL,
		enabled					TINYINT(1)      NOT NULL DEFAULT 1,
PRIMARY KEY(id)
);
ALTER TABLE evf_membership_user ENGINE = innodb;

CREATE UNIQUE INDEX ui_evf_membership_user_email
	ON evf_membership_user (emailAddress);

	
CREATE TABLE evf_membership_role(
        id						INT				NOT NULL AUTO_INCREMENT,
        oid                     VARCHAR(36)     NOT NULL,
        version                 INT				NOT NULL DEFAULT 0,
		token					NVARCHAR(64)	NOT NULL,
		description				NVARCHAR(512)	NOT NULL,
PRIMARY KEY(id)
);
ALTER TABLE evf_membership_user ENGINE = innodb;
CREATE UNIQUE INDEX ui_evf_membership_user_token
	ON evf_membership_role (token);

	
CREATE TABLE evf_membership_userrole(
        user_id                 INT				NOT NULL,
		role_id					INT				NOT NULL,
PRIMARY KEY(user_id, role_id)
);
ALTER TABLE evf_membership_userrole ENGINE = innodb;

ALTER TABLE evf_membership_userrole
        ADD CONSTRAINT  fk_evf_membership_user FOREIGN KEY (user_id) 
                REFERENCES evf_membership_user(id)
                ON DELETE CASCADE;
                
ALTER TABLE evf_membership_userrole
        ADD CONSTRAINT  fk_evf_membership_role FOREIGN KEY (role_id) 
                REFERENCES evf_membership_role(id)
                ON DELETE CASCADE;
