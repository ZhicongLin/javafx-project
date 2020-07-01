DROP TABLE "XZSP_DEV1"."REPO_FILE_SYNC_RESULT";
CREATE TABLE "XZSP_DEV1"."REPO_FILE_SYNC_RESULT"
(
    "UUID"           VARCHAR2(50 BYTE)  NOT NULL,
    "RESULT"         NUMBER(1)          NULL,
    "MESSAGE"        VARCHAR2(250 BYTE) NULL,
    "CHUNK_SIZE"     NUMBER(18)         NULL,
    "FILE_SIZE"      NUMBER(18)         NULL,
    "CREATE_TIME"    TIMESTAMP(6)       NOT NULL,
    "CREATOR"        VARCHAR2(50 BYTE)  NULL,
    "MODIFY_TIME"    TIMESTAMP(6)       NULL,
    "MODIFIER"       VARCHAR2(50 BYTE)  NULL,
    "REPO_FILE_UUID" VARCHAR2(50 BYTE)  NULL
)
    LOGGING
    NOCOMPRESS
    NOCACHE
;

CREATE UNIQUE INDEX "XZSP_DEV1"."FILE_UUID_RESULT_INDEX" ON "XZSP_DEV1"."REPO_FILE_SYNC_RESULT" ("RESULT" ASC, "REPO_FILE_UUID" ASC)
    LOGGING
    VISIBLE;

ALTER TABLE "XZSP_DEV1"."REPO_FILE_SYNC_RESULT"
    ADD CHECK ("UUID" IS NOT NULL);
ALTER TABLE "XZSP_DEV1"."REPO_FILE_SYNC_RESULT"
    ADD CHECK ("CREATE_TIME" IS NOT NULL);
ALTER TABLE "XZSP_DEV1"."REPO_FILE_SYNC_RESULT"
    ADD PRIMARY KEY ("UUID");
