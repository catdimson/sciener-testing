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
    public void create(Group instance) throws SQLException {
        groupRepository.create(instance);
    }

    @Override
    public void delete(int id) throws SQLException {
        groupRepository.delete(id);
    }

    @Override
    public void update(Group instance) throws SQLException {
        groupRepository.update(instance);
    }
}
