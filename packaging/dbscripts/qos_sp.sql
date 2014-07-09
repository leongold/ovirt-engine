----------------------------------------------------------------
-- [qos] Table
----------------------------------------------------------------

Create or replace FUNCTION InsertQos(v_id uuid,
  v_qos_type SMALLINT,
  v_name VARCHAR(50),
  v_description TEXT,
  v_storage_pool_id uuid,
  v_max_throughput INTEGER,
  v_max_read_throughput INTEGER,
  v_max_write_throughput INTEGER,
  v_max_iops INTEGER,
  v_max_read_iops INTEGER,
  v_max_write_iops INTEGER)
RETURNS VOID
   AS $procedure$
BEGIN
INSERT INTO qos(id, qos_type, name, description, storage_pool_id, max_throughput, max_read_throughput, max_write_throughput, max_iops, max_read_iops, max_write_iops)
  VALUES(v_id, v_qos_type, v_name, v_description, v_storage_pool_id, v_max_throughput, v_max_read_throughput, v_max_write_throughput, v_max_iops, v_max_read_iops, v_max_write_iops);
END; $procedure$
LANGUAGE plpgsql;

Create or replace FUNCTION UpdateQos(v_id uuid,
  v_qos_type SMALLINT,
  v_name VARCHAR(50),
  v_description TEXT,
  v_storage_pool_id uuid,
  v_max_throughput INTEGER,
  v_max_read_throughput INTEGER,
  v_max_write_throughput INTEGER,
  v_max_iops INTEGER,
  v_max_read_iops INTEGER,
  v_max_write_iops INTEGER)
RETURNS VOID
   AS $procedure$
BEGIN
      UPDATE qos
      SET qos_type = v_qos_type, name = v_name, description = v_description, storage_pool_id = v_storage_pool_id, max_throughput = v_max_throughput, max_read_throughput = v_max_read_throughput,
      max_write_throughput = v_max_write_throughput, max_iops = v_max_iops, max_read_iops = v_max_read_iops, max_write_iops = v_max_write_iops,
      _update_date = LOCALTIMESTAMP
      WHERE id = v_id;
END; $procedure$
LANGUAGE plpgsql;

Create or replace FUNCTION DeleteQos(v_id UUID) RETURNS VOID
   AS $procedure$
BEGIN
   DELETE FROM qos
   WHERE id = v_id;
END; $procedure$
LANGUAGE plpgsql;

Create or replace FUNCTION GetQosByQosId(v_id UUID) RETURNS SETOF qos STABLE
   AS $procedure$
BEGIN
RETURN QUERY SELECT *
   FROM qos
   WHERE id = v_id;
END; $procedure$
LANGUAGE plpgsql;

Create or replace FUNCTION GetAllQosForStoragePoolByQosType(v_storage_pool_id UUID, v_qos_type SMALLINT) RETURNS SETOF qos STABLE
   AS $procedure$
BEGIN
RETURN QUERY SELECT *
   FROM qos
   WHERE storage_pool_id = v_storage_pool_id
   AND qos_type = v_qos_type;
END; $procedure$
LANGUAGE plpgsql;

Create or replace FUNCTION GetAllQosForStoragePool(v_storage_pool_id UUID) RETURNS SETOF qos STABLE
   AS $procedure$
BEGIN
RETURN QUERY SELECT *
   FROM qos
   WHERE storage_pool_id = v_storage_pool_id;
END; $procedure$
LANGUAGE plpgsql;


Create or replace FUNCTION GetQosByDiskProfile(v_disk_profile_id UUID) RETURNS SETOF qos STABLE
   AS $procedure$
BEGIN
RETURN QUERY SELECT qos.*
   FROM qos
   JOIN disk_profiles ON qos.id = disk_profiles.qos_id
   WHERE disk_profiles.id = v_disk_profile_id;
END; $procedure$
LANGUAGE plpgsql;

