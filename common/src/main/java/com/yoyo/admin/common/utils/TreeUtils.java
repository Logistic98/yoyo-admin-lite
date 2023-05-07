package com.yoyo.admin.common.utils;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Tree相关工具类
 */
public class TreeUtils {

    /**
     * list转tree--主方法
     *
     * @param tags
     * @return
     */
    public static List<TreeNodeTag> listToTree(List<Tag> tags) {

        List<TreeNodeTag> lists = new ArrayList<>(tags.size());
        //先将查询出来的数据转换为TreeNodeTag节点的集合
        for (Tag tag : tags) {
            TreeNodeTag node = new TreeNodeTag();
            node.setId(tag.getId());
            node.setName(tag.getName());
            node.setParentId(tag.getParentId());
            lists.add(node);
        }
        //在关联孩子节点(若lists中只有一个顶级root节点，那么tree中只会有一个元素节点)
        List<TreeNodeTag> trees = new ArrayList<>();
        for (TreeNodeTag node : lists) {
            //寻找顶级root节点（一个List中，只有一个节点的父节点为null），如果取中间级作为root节点，需要传入中间级id
            if (node.getParentId() == null) {
                //添加孩子节点
                trees.add(findChildren(node, lists));
            }
        }

        return trees;
    }

    /**
     * list转tree--找到根节点下所有的孩子节点
     *
     * @param root
     * @param lists
     * @return
     */
    private static TreeNodeTag findChildren(TreeNodeTag root, List<TreeNodeTag> lists) {
        //遍历集合，寻找root节点的子节点（若节点的父节点为root节点id，则证明是root子节点）
        for (TreeNodeTag node : lists) {
            if (root.getId().equals(node.getParentId())) {
                //第一个节点匹配时，创造一个List集合，只会创建一次
                if (root.getChildren() == null) {
                    root.setChildren(new ArrayList<>());
                }
                //递归方式，创建该子节点。
                root.getChildren().add(findChildren(node, lists));
            }
        }
        return root;
    }

    /**
     * tree转list--主方法
     *
     * @param tree
     * @return
     */
    public static List<Tag> treeToList(List<TreeNodeTag> tree) {
        ArrayList<Tag> list = new ArrayList<>();
        treeToList(tree, list);
        return list;
    }

    /**
     * 将tree转换成list
     *
     * @param tree
     * @param result
     */
    private static void treeToList(List<TreeNodeTag> tree, List<Tag> result) {
        for (TreeNodeTag node : tree) {
            //读取到根节点时，将数据放入到list中
            Tag tag = new Tag();
            tag.setId(node.getId());
            tag.setName(node.getName());
            tag.setParentId(node.getParentId());
            result.add(tag);
            List<TreeNodeTag> children = node.getChildren();
            //递归出口，节点不为空时，一直去遍历
            if (!CollectionUtils.isEmpty(children)) {
                treeToList(children, result);
            }
        }
    }

    /**
     * 根据id找到对应的node节点--主方法
     *
     * @param id
     * @param tree
     * @return
     */
    public static TreeNodeTag findChildrenNodes(String id, List<TreeNodeTag> tree) {
        //遍历tree找到节点
        for (TreeNodeTag node : tree) {
            if (node.getId().equals(id)) {
                return node;
            } else {
                //获取子节点列表
                List<TreeNodeTag> children = node.getChildren();
                if (!CollectionUtils.isEmpty(children)) {
                    TreeNodeTag childrenNodes = findChildrenNodes(id, children);
                    //没有找到节点（即等于null），那么就继续for循环去兄弟节点处寻找
                    if (childrenNodes != null) {
                        return childrenNodes;
                    }
                }
            }
        }
        return null;
    }

    /**
     * 查询给定节点列表的所有孩子节点id
     *
     * @param ids
     * @param tree
     * @return
     */
    public static List<String> findChildNodes(List<String> ids, List<TreeNodeTag> tree) {
        //查找给定id对应的node节点
        List<String> result = new ArrayList<>();
        for (String id : ids) {
            //根据id获取到node节点
            TreeNodeTag root = findChildrenNodes(id, tree);
            //获取到node节点上的所以孩子节点信息
            if (root == null) {
                continue;
            }
            //获取到节点信息
            List<TreeNodeTag> children = root.getChildren();
            List<String> res = new ArrayList<>();
            if (!CollectionUtils.isEmpty(children)) {
                //前序遍历节点信息
                getSubNodeId(children, res);
            }
            //获取到数据集合
            result.addAll(res);

        }
        return result;
    }

    /**
     * 前序遍历获取子节点信息
     *
     * @param roots
     * @param res
     */
    private static void getSubNodeId(List<TreeNodeTag> roots, List<String> res) {
        if (!CollectionUtils.isEmpty(roots)) {
            for (TreeNodeTag node : roots) {
                //第一次获取到根节点时处理数据
                res.add(node.getId());
                getSubNodeId(node.getChildren(), res);
            }
        }
    }

    /**
     * 根据传入的多叉树集合获取到所有叶子节点信息--主方法
     *
     * @param trees
     * @return
     */
    public static List<TreeNodeTag> findLeafNodes(List<TreeNodeTag> trees) {

        //生成一个结果集保存叶子节点
        List<TreeNodeTag> leafNodes = new ArrayList<>();
        findLeafNodes(trees, leafNodes);
        return leafNodes;

    }

    /**
     * 根据传入的多叉树集合获取到所有叶子节点信息
     *
     * @param trees
     * @param result
     */
    public static void findLeafNodes(List<TreeNodeTag> trees, List<TreeNodeTag> result) {

        //在trees不为null的情况下，foreach遍历
        if (trees != null) {
            for (TreeNodeTag node : trees) {
                //若是node节点的子节点为空，那么将结果保存到集合中
                if (CollectionUtils.isEmpty(node.getChildren())) {
                    result.add(node);
                }
                //继续遍历
                findLeafNodes(node.getChildren(), result);
            }

        }
    }

    /**
     * 实体类,List中的元素（数据库存储的实体）
     */
    @Data
    private static class Tag {
        private String id;  //节点编号
        private String name; //节点名字
        private String parentId;  //节点父级编号
    }

    /**
     * 树中的节点信息
     */
    @Data
    private static class TreeNodeTag {

        private String id;
        private String name;
        private String parentId;
        private List<TreeNodeTag> children;

    }


    public static void main(String[] args) {

        // 初始化测试数据
        Tag tag = new Tag();
        tag.setId("1");
        tag.setName("河北省");
        Tag tag1 = new Tag();
        tag1.setId("1-1");
        tag1.setName("沧州市");
        tag1.setParentId("1");
        Tag tag2 = new Tag();
        tag2.setId("1-2");
        tag2.setName("邯郸市");
        tag2.setParentId("1");
        Tag tag3 = new Tag();
        tag3.setId("1-3");
        tag3.setName("衡水市");
        tag3.setParentId("1");
        Tag tag4 = new Tag();
        tag4.setId("1-3-1");
        tag4.setName("景县");
        tag4.setParentId("1-3");
        Tag tag5 = new Tag();
        tag5.setId("1-3-2");
        tag5.setName("桃城区");
        tag5.setParentId("1-3");
        List<Tag> tagList=new ArrayList<>();
        tagList.add(tag);
        tagList.add(tag1);
        tagList.add(tag2);
        tagList.add(tag3);
        tagList.add(tag4);
        tagList.add(tag5);

        System.out.println("1. 测试list转tree");
        // 可以看做是多叉树的生成，采用后序遍历的方式，即先遍历子节点，在填充根节点。
        List<TreeNodeTag> tree = listToTree(tagList);
        System.out.println(JSON.toJSON(tree));

        System.out.println("2. 测试tree转list");
        // 采用的是前序遍历的方式，第一次遍历到根节点时，将数据放入到list集合中。
        List<Tag> list = treeToList(tree);
        System.out.println(list);

        System.out.println("3. 根据id查找对应的node节点");
        // 对元素采取的是前序遍历的方式，即第一次遍历到根节点时，判断根节点的值是否为传入的id值。
        // 若不是，那么获取到根节点的子节点。然后遍历各个子节点。只有到子节点返回真正数据时，才return出去，否则的话去兄弟节点寻找数据。
        TreeNodeTag treeNodeTag = findChildrenNodes("1-3-2", tree);
        System.out.println(treeNodeTag);

        System.out.println("4. 根据节点编号查询节点的子节点信息");
        // 根据id找到对应node节点后，使用前序遍历获取节点信息。
        List<String> ids = new ArrayList<>();
        ids.add("1-2");
        ids.add("1-3");
        List<String> childrenIds = findChildNodes(ids, tree);
        System.out.println(childrenIds);

        System.out.println("5. 查询节点的叶子节点信息");
        // 叶子节点特征是其的子节点集合为空。
        List<TreeNodeTag> leafNodes = findLeafNodes(tree);
        System.out.println(leafNodes);

    }
}