package com.aoindustries.aoserv.client;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */

/**
 * @see  Reseller
 *
 * @author  AO Industries, Inc.
 */
@ServiceAnnotation(ServiceName.resellers)
public interface ResellerService<C extends AOServConnector<C,F>, F extends AOServConnectorFactory<C,F>> extends AOServServiceAccountingCodeKey<C,F,Reseller> {

    /**
     * Gets the list of all resellers that either have a null parent (the
     * actual root of the business tree) or where the parent is inaccessible.
     */
    /* TODO
    public List<Reseller> getTopLevelResellers() throws IOException, SQLException {
        List<Reseller> matches=new ArrayList<Reseller>();
        for(Reseller reseller : getRows()) {
            if(reseller.getParentReseller()==null) matches.add(reseller);
        }
        return matches;
    }
    */
    // <editor-fold defaultstate="collapsed" desc="Tree compatibility">
    // TODO
//    private final Tree<Reseller> tree = new Tree<Reseller>() {
//        public List<Node<Reseller>> getRootNodes() throws IOException, SQLException {
//            List<Reseller> topLevelResellers = getTopLevelResellers();
//            int size = topLevelResellers.size();
//            if(size==0) {
//                return Collections.emptyList();
//            } else if(size==1) {
//                Node<Reseller> singleNode = new ResellerTreeNode(topLevelResellers.get(0));
//                return Collections.singletonList(singleNode);
//            } else {
//                List<Node<Reseller>> rootNodes = new ArrayList<Node<Reseller>>(size);
//                for(Reseller topLevelReseller : topLevelResellers) rootNodes.add(new ResellerTreeNode(topLevelReseller));
//                return Collections.unmodifiableList(rootNodes);
//            }
//        }
//    };
//
//    static class ResellerTreeNode implements Node<Reseller> {
//
//        private final Reseller reseller;
//
//        ResellerTreeNode(Reseller reseller) {
//            this.reseller = reseller;
//        }
//
//        /**
//         * The children of the reseller are any resellers that have their closest parent
//         * business (that is a reseller) equal to this one.
//         */
//        public List<Node<Reseller>> getChildren() throws IOException, SQLException {
//            // Look for any existing children
//            List<Reseller> children = reseller.getChildResellers();
//            int size = children.size();
//            if(size==0) {
//                // Any reseller without children is rendered as not being able to have children.
//                return null;
//            } else if(size==1) {
//                Node<Reseller> singleNode = new ResellerTreeNode(children.get(0));
//                return Collections.singletonList(singleNode);
//            } else {
//                List<Node<Reseller>> childNodes = new ArrayList<Node<Reseller>>(size);
//                for(Reseller child : children) childNodes.add(new ResellerTreeNode(child));
//                return Collections.unmodifiableList(childNodes);
//            }
//        }
//
//        public Reseller getValue() {
//            return reseller;
//        }
//    }
//
//    /**
//     * Gets a Tree view of all the accessible resellers.
//     * All access to the tree read-through to the underlying storage
//     * and are thus subject to change at any time.  If you need a consistent
//     * snapshot of the tree, consider using TreeCopy.
//     *
//     * @see  TreeCopy
//     */
//    public Tree<Reseller> getTree() {
//        return tree;
//    }
    // </editor-fold>
}
