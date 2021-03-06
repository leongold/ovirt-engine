CREATE INDEX idx_repo_file_meta_data_repo_domain_id ON repo_file_meta_data (repo_domain_id);
CREATE INDEX idx_vm_static_large_icon_id ON vm_static (large_icon_id);
CREATE INDEX idx_vds_spm_id_map_vds_id ON vds_spm_id_map (vds_id);
CREATE INDEX idx_vm_host_pinning_map_vds_id ON vm_host_pinning_map (vds_id);
CREATE INDEX idx_gluster_georep_session_master_volume_id ON gluster_georep_session (master_volume_id);
CREATE INDEX idx_tags_vm_map_vm_id ON tags_vm_map (vm_id);
CREATE INDEX idx_event_subscriber_subscriber_id ON event_subscriber (subscriber_id);
CREATE INDEX idx_network_attachments_network_id ON network_attachments (network_id);
CREATE INDEX idx_vm_interface_vmt_guid ON vm_interface (vmt_guid);
CREATE INDEX idx_async_tasks_entities_async_task_id ON async_tasks_entities (async_task_id);
CREATE INDEX idx_vm_static_provider_id ON vm_static (provider_id);
CREATE INDEX idx_vm_dynamic_migrating_to_vds ON vm_dynamic (migrating_to_vds);
CREATE INDEX idx_cpu_profiles_qos_id ON cpu_profiles (qos_id);
CREATE INDEX idx_host_device_vm_id ON host_device (vm_id);
CREATE INDEX idx_vds_groups_storage_pool_id ON vds_groups (storage_pool_id);
CREATE INDEX idx_gluster_volume_snapshot_config_cluster_id ON gluster_volume_snapshot_config (cluster_id);
CREATE INDEX idx_gluster_volume_snapshot_config_volume_id ON gluster_volume_snapshot_config (volume_id);
CREATE INDEX idx_network_cluster_cluster_id ON network_cluster (cluster_id);
CREATE INDEX idx_host_device_host_id_parent_device_name ON host_device (host_id, parent_device_name);
CREATE INDEX idx_event_notification_hist_audit_log_id ON event_notification_hist (audit_log_id);
CREATE INDEX idx_affinity_group_members_affinity_group_id ON affinity_group_members (affinity_group_id);
CREATE INDEX idx_gluster_georep_config_session_id ON gluster_georep_config (session_id);
CREATE INDEX idx_supported_cluster_features_cluster_id ON supported_cluster_features (cluster_id);
CREATE INDEX idx_vm_static_vds_group_id ON vm_static (vds_group_id);
CREATE INDEX idx_vm_icon_defaults_small_icon_id ON vm_icon_defaults (small_icon_id);
CREATE INDEX idx_storage_pool_iso_map_storage_pool_id ON storage_pool_iso_map (storage_pool_id);
CREATE INDEX idx_host_device_host_id ON host_device (host_id);
CREATE INDEX idx_vm_static_quota_id ON vm_static (quota_id);
CREATE INDEX idx_iscsi_bonds_networks_map_iscsi_bond_id ON iscsi_bonds_networks_map (iscsi_bond_id);
CREATE INDEX idx_cluster_policy_units_policy_unit_id ON cluster_policy_units (policy_unit_id);
CREATE INDEX idx_gluster_services_service_type ON gluster_services (service_type);
CREATE INDEX idx_vds_static_vds_group_id ON vds_static (vds_group_id);
CREATE INDEX idx_supported_host_features_host_id ON supported_host_features (host_id);
CREATE INDEX idx_vm_interface_statistics_vm_id ON vm_interface_statistics (vm_id);
CREATE INDEX idx_vds_static_host_provider_id ON vds_static (host_provider_id);
CREATE INDEX idx_iscsi_bonds_storage_connections_map_iscsi_bond_id ON iscsi_bonds_storage_connections_map (iscsi_bond_id);
CREATE INDEX idx_tags_vds_map_tag_id ON tags_vds_map (tag_id);
CREATE INDEX idx_job_subject_entity_job_id ON job_subject_entity (job_id);
CREATE INDEX idx_gluster_server_services_service_id ON gluster_server_services (service_id);
CREATE INDEX idx_vds_interface_statistics_vds_id ON vds_interface_statistics (vds_id);
CREATE INDEX idx_lun_storage_server_connection_map_lun_id ON lun_storage_server_connection_map (lun_id);
CREATE INDEX idx_affinity_group_members_vm_id ON affinity_group_members (vm_id);
CREATE INDEX idx_network_qos_id ON network (qos_id);
CREATE INDEX idx_storage_domains_ovf_info_storage_domain_id ON storage_domains_ovf_info (storage_domain_id);
CREATE INDEX idx_tags_user_map_tag_id ON tags_user_map (tag_id);
CREATE INDEX idx_tags_vds_map_vds_id ON tags_vds_map (vds_id);
CREATE INDEX idx_iscsi_bonds_storage_connections_map_connection_id ON iscsi_bonds_storage_connections_map (connection_id);
CREATE INDEX idx_vm_static_vmt_guid ON vm_static (vmt_guid);
CREATE INDEX idx_roles_groups_role_id ON roles_groups (role_id);
CREATE INDEX idx_vm_icon_defaults_large_icon_id ON vm_icon_defaults (large_icon_id);
CREATE INDEX idx_cluster_policy_units_cluster_policy_id ON cluster_policy_units (cluster_policy_id);
CREATE INDEX idx_disk_lun_map_lun_id ON disk_lun_map (lun_id);
CREATE INDEX idx_iscsi_bonds_storage_pool_id ON iscsi_bonds (storage_pool_id);
CREATE INDEX idx_vm_pool_map_vm_pool_id ON vm_pool_map (vm_pool_id);
CREATE INDEX idx_command_assoc_entities_command_id ON command_assoc_entities (command_id);
CREATE INDEX idx_gluster_cluster_services_service_type ON gluster_cluster_services (service_type);
CREATE INDEX idx_tags_user_map_user_id ON tags_user_map (user_id);
CREATE INDEX idx_tags_vm_pool_map_tag_id ON tags_vm_pool_map (tag_id);
CREATE INDEX idx_gluster_server_hooks_hook_id ON gluster_server_hooks (hook_id);
CREATE INDEX idx_iscsi_bonds_networks_map_network_id ON iscsi_bonds_networks_map (network_id);
CREATE INDEX idx_disk_profiles_qos_id ON disk_profiles (qos_id);
CREATE INDEX idx_network_provider_network_provider_id ON network (provider_network_provider_id);
CREATE INDEX idx_gluster_georep_session_details_master_brick_id ON gluster_georep_session_details (master_brick_id);
CREATE INDEX idx_tags_vm_pool_map_vm_pool_id ON tags_vm_pool_map (vm_pool_id);
CREATE INDEX idx_mac_pool_ranges_mac_pool_id ON mac_pool_ranges (mac_pool_id);
CREATE INDEX idx_image_storage_domain_map_quota_id ON image_storage_domain_map (quota_id);
CREATE INDEX idx_vfs_config_networks_network_id ON vfs_config_networks (network_id);
CREATE INDEX idx_disk_lun_map_disk_id ON disk_lun_map (disk_id);
CREATE INDEX idx_vm_host_pinning_map_vm_id ON vm_host_pinning_map (vm_id);
CREATE INDEX idx_gluster_georep_session_details_session_id ON gluster_georep_session_details (session_id);
CREATE INDEX idx_gluster_volume_bricks_server_id ON gluster_volume_bricks (server_id);
CREATE INDEX idx_host_device_host_id_physfn ON host_device (host_id, physfn);
CREATE INDEX idx_tags_user_group_map_tag_id ON tags_user_group_map (tag_id);
CREATE INDEX idx_snapshots_vm_id ON snapshots (vm_id);
CREATE INDEX idx_network_cluster_network_id ON network_cluster (network_id);
CREATE INDEX idx_vds_groups_cluster_policy_id ON vds_groups (cluster_policy_id);
CREATE INDEX idx_vds_spm_id_map_storage_pool_id ON vds_spm_id_map (storage_pool_id);
CREATE INDEX idx_gluster_volume_snapshots_volume_id ON gluster_volume_snapshots (volume_id);
CREATE INDEX idx_image_storage_domain_map_storage_domain_id ON image_storage_domain_map (storage_domain_id);
CREATE INDEX idx_vm_device_snapshot_id ON vm_device (snapshot_id);
CREATE INDEX idx_tags_vm_map_tag_id ON tags_vm_map (tag_id);
CREATE INDEX idx_tags_user_group_map_group_id ON tags_user_group_map (group_id);
CREATE INDEX idx_gluster_server_hooks_server_id ON gluster_server_hooks (server_id);
CREATE INDEX idx_vm_pools_vds_group_id ON vm_pools (vds_group_id);
CREATE INDEX idx_storage_pool_iso_map_storage_id ON storage_pool_iso_map (storage_id);
CREATE INDEX idx_gluster_volume_bricks_network_id ON gluster_volume_bricks (network_id);
CREATE INDEX idx_vm_interface_vm_guid ON vm_interface (vm_guid);
CREATE INDEX idx_lun_storage_server_connection_map_storage_server_connection ON lun_storage_server_connection_map (storage_server_connection);
CREATE INDEX idx_vm_static_small_icon_id ON vm_static (small_icon_id);
CREATE INDEX idx_supported_cluster_features_feature_id ON supported_cluster_features (feature_id);
CREATE INDEX idx_storage_pool_mac_pool_id ON storage_pool (mac_pool_id);
