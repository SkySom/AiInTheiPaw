package io.sommers.ai.repository;

import com.google.cloud.spring.data.firestore.FirestoreReactiveRepository;
import io.sommers.ai.model.channel.ChannelData;
import org.springframework.stereotype.Repository;

@Repository
public interface IChannelDataRepository extends FirestoreReactiveRepository<ChannelData> {
}
