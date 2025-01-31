CREATE TABLE public.probation_teams (
  id uuid DEFAULT gen_random_uuid() PRIMARY KEY NOT NULL,
  team_code character varying NOT NULL,
  local_delivery_unit_mailbox_id uuid NOT NULL,
  email_address character varying NOT NULL,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_local_delivery_unit_mailbox
      FOREIGN KEY (local_delivery_unit_mailbox_id)
          REFERENCES public.local_delivery_unit_mailboxes(id)
);