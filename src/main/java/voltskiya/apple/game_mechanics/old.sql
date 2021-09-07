SELECT nearby.*,
       stored_mob.unique_name,
       count(stored_mob.mob_my_uid) as mob_count
FROM (SELECT center.chunk_uid as cchunk_uid,
             chunk.biome_guess_uid,
             chunk.world_my_uid,
             center.chunk_x,
             center.chunk_z,
             center.middle_x,
             center.middle_y,
             center.middle_z,
             bridge.chunk_uid as bridge_uid
      FROM (SELECT *
            FROM contour
            ORDER BY rand()
            LIMIT 40) center
               INNER JOIN
           chunk
           ON chunk.chunk_uid = center.chunk_uid
               INNER JOIN
           contour bridge
           ON bridge.chunk_uid IN (
                                   center.bridge_x_neg,
                                   center.bridge_x_pos,
                                   center.bridge_z_neg,
                                   center.bridge_z_pos,
                                   center.chunk_uid)
               INNER JOIN
           contour bridge2
           ON bridge2.chunk_uid IN (
                                    bridge.bridge_x_neg,
                                    bridge.bridge_x_pos,
                                    bridge.bridge_z_neg,
                                    bridge.bridge_z_pos,
                                    bridge.chunk_uid)) nearby
         LEFT JOIN
     stored_mob
     ON floor(stored_mob.x / 16) = nearby.chunk_x
         AND floor(stored_mob.z / 16) = nearby.chunk_z
GROUP BY stored_mob.unique_name,
         nearby.cchunk_uid
ORDER BY rand()
LIMIT 5