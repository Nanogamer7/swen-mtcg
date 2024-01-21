--
-- PostgreSQL database dump
--

-- Dumped from database version 16.0 (Debian 16.0-1.pgdg120+1)
-- Dumped by pg_dump version 16.1

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: cardtype; Type: TYPE; Schema: public; Owner: postgres
--

CREATE TYPE public.cardtype AS ENUM (
    'monster',
    'spell'
);


ALTER TYPE public.cardtype OWNER TO postgres;

--
-- Name: delete_package(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.delete_package() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
    DELETE FROM packages
    WHERE uuid = OLD.package;
    RETURN OLD;
END;
$$;


ALTER FUNCTION public.delete_package() OWNER TO postgres;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: cards; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.cards (
    uuid uuid DEFAULT gen_random_uuid() NOT NULL,
    damage double precision DEFAULT 0 NOT NULL,
    owner uuid,
    deck boolean DEFAULT false NOT NULL,
    trade boolean DEFAULT false NOT NULL,
    package uuid,
    name character varying,
    CONSTRAINT "trade or deck" CHECK (((deck IS FALSE) OR (trade IS FALSE)))
);


ALTER TABLE public.cards OWNER TO postgres;

--
-- Name: packages; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.packages (
    uuid uuid DEFAULT gen_random_uuid() NOT NULL,
    created_at timestamp without time zone DEFAULT now()
);


ALTER TABLE public.packages OWNER TO postgres;

--
-- Name: trades; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.trades (
    uuid uuid DEFAULT gen_random_uuid() NOT NULL,
    card uuid NOT NULL,
    card_type public.cardtype NOT NULL,
    min_dmg double precision NOT NULL,
    user_uuid uuid
);


ALTER TABLE public.trades OWNER TO postgres;

--
-- Name: users; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.users (
    uuid uuid DEFAULT gen_random_uuid() NOT NULL,
    username character varying NOT NULL,
    password character varying NOT NULL,
    name character varying,
    bio character varying,
    image character varying,
    elo integer DEFAULT 100 NOT NULL,
    wins integer DEFAULT 0 NOT NULL,
    losses integer DEFAULT 0 NOT NULL,
    coins integer DEFAULT 20 NOT NULL,
    admin boolean DEFAULT false
);


ALTER TABLE public.users OWNER TO postgres;

--
-- Data for Name: cards; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: packages; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: trades; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Name: cards cards_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.cards
    ADD CONSTRAINT cards_pk PRIMARY KEY (uuid);


--
-- Name: packages packages_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.packages
    ADD CONSTRAINT packages_pk PRIMARY KEY (uuid);


--
-- Name: trades trades_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.trades
    ADD CONSTRAINT trades_pk PRIMARY KEY (uuid);


--
-- Name: users user_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT user_pk PRIMARY KEY (uuid);


--
-- Name: users username; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT username UNIQUE (username);


--
-- Name: cards delete_package_trigger; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER delete_package_trigger AFTER UPDATE ON public.cards FOR EACH ROW WHEN (((new.package IS NULL) AND (old.package IS NOT NULL))) EXECUTE FUNCTION public.delete_package();


--
-- Name: cards cards_packages_uuid_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.cards
    ADD CONSTRAINT cards_packages_uuid_fk FOREIGN KEY (package) REFERENCES public.packages(uuid) ON DELETE SET NULL;


--
-- Name: cards cards_users_uuid_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.cards
    ADD CONSTRAINT cards_users_uuid_fk FOREIGN KEY (owner) REFERENCES public.users(uuid) ON DELETE SET NULL;


--
-- Name: trades trades_cards_uuid_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.trades
    ADD CONSTRAINT trades_cards_uuid_fk FOREIGN KEY (card) REFERENCES public.cards(uuid);


--
-- Name: trades trades_users_uuid_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.trades
    ADD CONSTRAINT trades_users_uuid_fk FOREIGN KEY (user_uuid) REFERENCES public.users(uuid);


--
-- PostgreSQL database dump complete
--

