
-- Default membership roles
insert into evf_membership_role(oid, token, description) values(uuid(), 'evf.membership.viewProfile', '');
insert into evf_membership_role(oid, token, description) values(uuid(), 'evf.membership.logout', '');
