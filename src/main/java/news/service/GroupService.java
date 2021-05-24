package news.service;

import news.dao.repositories.GroupRepository;
import news.dao.specifications.ExtendSqlSpecification;
import news.model.Group;

import java.sql.SQLException;
import java.util.List;

public class GroupService implements Service<Group> {
    final private GroupRepository groupRepository;

    public GroupService(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    @Override
    public List<Group> query(ExtendSqlSpecification<Group> specification) throws SQLException {
        return groupRepository.query(specification);
    }

    @Override
    public int create(Group instance) throws SQLException {
        return groupRepository.create(instance);
    }

    @Override
    public int delete(int id) throws SQLException {
        return groupRepository.delete(id);
    }

    @Override
    public int update(Group instance) throws SQLException {
        return groupRepository.update(instance);
    }
}