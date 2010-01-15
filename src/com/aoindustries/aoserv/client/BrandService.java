/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
package com.aoindustries.aoserv.client;

/**
 * @see  Brand
 *
 * @author  AO Industries, Inc.
 */
@ServiceAnnotation(ServiceName.brands)
public interface BrandService<C extends AOServConnector<C,F>, F extends AOServConnectorFactory<C,F>> extends AOServServiceAccountingCodeKey<C,F,Brand> {

    /**
     * Gets the list of all brands that either have a null parent (the
     * actual root of the business tree) or where the parent is inaccessible.
     */
    /* TODO
    public List<Brand> getTopLevelBrands() throws IOException, SQLException {
        List<Brand> matches=new ArrayList<Brand>();
        for(Brand brand : getRows()) {
            if(brand.getParentBrand()==null) matches.add(brand);
        }
        return matches;
    }
    */
    // <editor-fold defaultstate="collapsed" desc="Tree compatibility">
    // TODO
//    private final Tree<Brand> tree = new Tree<Brand>() {
//        public List<Node<Brand>> getRootNodes() throws IOException, SQLException {
//            List<Brand> topLevelBrands = getTopLevelBrands();
//            int size = topLevelBrands.size();
//            if(size==0) {
//                return Collections.emptyList();
//            } else if(size==1) {
//                Node<Brand> singleNode = new BrandTreeNode(topLevelBrands.get(0));
//                return Collections.singletonList(singleNode);
//            } else {
//                List<Node<Brand>> rootNodes = new ArrayList<Node<Brand>>(size);
//                for(Brand topLevelBrand : topLevelBrands) rootNodes.add(new BrandTreeNode(topLevelBrand));
//                return Collections.unmodifiableList(rootNodes);
//            }
//        }
//    };
//
//    static class BrandTreeNode implements Node<Brand> {
//
//        private final Brand brand;
//
//        BrandTreeNode(Brand brand) {
//            this.brand = brand;
//        }
//
//        /**
//         * The children of the brand are any brands that have their closest parent
//         * business (that is a brand) equal to this one.
//         */
//        public List<Node<Brand>> getChildren() throws IOException, SQLException {
//            // Look for any existing children
//            List<Brand> children = brand.getChildBrands();
//            int size = children.size();
//            if(size==0) {
//                // Any brand without children is rendered as not being able to have children.
//                return null;
//            } else if(size==1) {
//                Node<Brand> singleNode = new BrandTreeNode(children.get(0));
//                return Collections.singletonList(singleNode);
//            } else {
//                List<Node<Brand>> childNodes = new ArrayList<Node<Brand>>(size);
//                for(Brand child : children) childNodes.add(new BrandTreeNode(child));
//                return Collections.unmodifiableList(childNodes);
//            }
//        }
//
//        public Brand getValue() {
//            return brand;
//        }
//    }
//
//    /**
//     * Gets a Tree view of all the accessible brands.
//     * All access to the tree read-through to the underlying storage
//     * and are thus subject to change at any time.  If you need a consistent
//     * snapshot of the tree, consider using TreeCopy.
//     *
//     * @see  TreeCopy
//     */
//    public Tree<Brand> getTree() {
//        return tree;
//    }
    // </editor-fold>
}
