/*
 * Copyright (c) 2003 Regents of the University of California.
 * All rights reserved.
 *
 * This software was developed at the University of California, Irvine.
 *
 * Redistribution and use in source and binary forms are permitted
 * provided that the above copyright notice and this paragraph are
 * duplicated in all such forms and that any documentation,
 * advertising materials, and other materials related to such
 * distribution and use acknowledge that the software was developed
 * by the University of California, Irvine.  The name of the
 * University may not be used to endorse or promote products derived
 * from this software without specific prior written permission.
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
 * WARRANTIES OF MERCHANTIBILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 */
package c2demo.coalitiondemo.xadl;

import edu.uci.isr.xarch.*;
import edu.uci.isr.xarch.instance.*;
import edu.uci.isr.xarch.types.*;
import edu.uci.isr.xarch.implementation.*;
import edu.uci.isr.xarch.variants.*;
import edu.uci.isr.xarch.javaimplementation.*;
import edu.uci.isr.xarch.javainitparams.*;
import edu.uci.isr.xarch.boolguard.*;
import edu.uci.isr.xarch.versions.*;
import edu.uci.isr.xarch.messages.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.io.FileOutputStream;
import java.io.PrintStream;

/**
 * This class will emit the XML format of the architecture that can be
 * edited with ArchStudio or used to bootstrap the project.
 */
public class Description {

    /* These are the factory objects which all variables must be
     * instantiated from.
     */
    protected InstanceContext instance;
    protected TypesContext types;
    protected ImplementationContext implementation;
    protected VariantsContext variants;
    protected BoolguardContext boolguards;
    protected JavaimplementationContext javaimplementation;
    protected JavainitparamsContext javainitparams;
    protected VersionsContext versions;
    protected MessagesContext messages;

    /**
     * The overall architecture object.
     *
     * All of the factories are tied to the architecture object, so it must
     * be controlled privately and part of this class.
     */
    protected IXArch xArch;

    /**
     * Default constructor
     */
    public Description() {
        xArch = XArchUtils.createXArch();
        instance = new InstanceContext(xArch);
        types = new TypesContext(xArch);
        implementation = new ImplementationContext(xArch);
        variants = new VariantsContext(xArch);
        boolguards = new BoolguardContext(xArch);
        javaimplementation = new JavaimplementationContext(xArch);
        javainitparams = new JavainitparamsContext(xArch);
        versions = new VersionsContext(xArch);
        messages = new MessagesContext(xArch);
    }

    /**
     * Helper function to create the IDescription object.
     */
    protected IDescription createDescription(String value) {
        IDescription desc = types.createDescription();
        desc.setValue(value);
        return desc;
    }

    /**
     * Create a link between two ids.
     */
    protected ILink createLink(String topId, String bottomId) {
        ILink link = types.createLink();
        link.setId(topId + "_to_" + bottomId);
        link.setDescription(createDescription(topId + " to " + bottomId +
                                              " Link"));

        IPoint point1 = types.createPoint();
        IXMLLink anchor1 = instance.createXMLLink();
        anchor1.setType("simple");
        anchor1.setHref("#" + topId + ".IFACE_BOTTOM");
        point1.setAnchorOnInterface(anchor1);

        IPoint point2 = types.createPoint();
        IXMLLink anchor2 = instance.createXMLLink();
        anchor2.setType("simple");
        anchor2.setHref("#" + bottomId + ".IFACE_TOP");
        point2.setAnchorOnInterface(anchor2);

        link.addPoint(point1);
        link.addPoint(point2);
        return link;
    }

    /**
     * Create a special link used with Fred Connectors.
     * @see createLink
     */
    protected ILink createFredLink(String topId, String bottomId,
                                 String FredId) {

        ILink link = types.createLink();
        IPoint point1 = types.createPoint();
        IXMLLink anchor1 = instance.createXMLLink();

        if (bottomId != null){
            link.setId(FredId + "_to_" + bottomId);
            link.setDescription(createDescription(FredId + " to " + bottomId +
                                                  " Link"));
            anchor1.setType("simple");
            anchor1.setHref("#" + bottomId + ".IFACE_TOP");
            point1.setAnchorOnInterface(anchor1);
        } else if (topId != null) {
            link.setId(topId + "_to_" + FredId);
            link.setDescription(createDescription(topId + " to " + FredId +
                                                  " Link"));
            anchor1.setType("simple");
            anchor1.setHref("#" + topId + ".IFACE_BOTTOM");
            point1.setAnchorOnInterface(anchor1);
        }

        IPoint point2 = types.createPoint();
        IXMLLink anchor2 = instance.createXMLLink();
        anchor2.setType("simple");
        anchor2.setHref("#" + FredId + ".IFACE_LOCAL");
        point2.setAnchorOnInterface(anchor2);

        link.addPoint(point1);
        link.addPoint(point2);
        return link;
    }

    /**
     * Create the initialization parameters
     */
    protected IInitializationParameter createInitializationParameter(String name,
                                                               String value) {
        IInitializationParameter ip = javainitparams.createInitializationParameter();
        ip.setName(name);
        ip.setValue(value);
        return ip;
    }

    protected IJavaImplementation createJavaImplementation(String javaClassName,
                                       IInitializationParameter[] initparams) {
        IJavaImplementation javaImplementation =
            javaimplementation.createJavaImplementation();
        IJavaClassFile javaClassFile;

        if(initparams != null){
            javaClassFile = javainitparams.createJavaClassFileParams();
            ((IJavaClassFileParams)javaClassFile).
                addInitializationParameters(Arrays.asList(initparams));
        }
        else{
            javaClassFile = javaimplementation.createJavaClassFile();
        }

        IJavaClassName jcn = javaimplementation.createJavaClassName();
        jcn.setValue(javaClassName);
        javaClassFile.setJavaClassName(jcn);
        javaImplementation.setMainClass(javaClassFile);

        return javaImplementation;
    }

    protected IComponent createComponent(String id, String description,
                                       String typeId){
        IComponent c = types.createComponent();

        c.setId(id);
        IDescription desc = types.createDescription();
        desc.setValue(description);
        c.setDescription(desc);

        IInterface topIface = types.createInterface();
        topIface.setId(id + ".IFACE_TOP");
        topIface.setDescription(createDescription(description +
                                                  " Top Interface"));
        IDirection topDirection = types.createDirection();
        topDirection.setValue("inout");
        topIface.setDirection(topDirection);

        IXMLLink topIfaceType = types.createXMLLink();
        topIfaceType.setType("simple");
        topIfaceType.setHref("#C2TopType");

        topIface.setType(topIfaceType);

        IInterface bottomIface = types.createInterface();
        bottomIface.setId(id + ".IFACE_BOTTOM");
        bottomIface.setDescription(createDescription(description +
                                                     " Bottom Interface"));
        IDirection bottomDirection = types.createDirection();
        bottomDirection.setValue("inout");
        bottomIface.setDirection(bottomDirection);

        IXMLLink bottomIfaceType = types.createXMLLink();
        bottomIfaceType.setType("simple");
        bottomIfaceType.setHref("#C2BottomType");

        bottomIface.setType(bottomIfaceType);

        c.addInterface(topIface);
        c.addInterface(bottomIface);

        IXMLLink cType = types.createXMLLink();
        cType .setType("simple");
        cType .setHref("#" + typeId);
        c.setType(cType);
        return c;
    }

    protected IConnector createConnector(String id, String description,
                                         String typeId) {
        IConnector c = types.createConnector();

        c.setId(id);
        IDescription desc = types.createDescription();
        desc.setValue(description);
        c.setDescription(desc);

        IInterface topIface = types.createInterface();
        topIface.setId(id + ".IFACE_TOP");
        topIface.setDescription(createDescription(description +
                                                  " Top Interface"));
        IDirection topDirection = types.createDirection();
        topDirection.setValue("inout");
        topIface.setDirection(topDirection);

        IXMLLink topIfaceType = types.createXMLLink();
        topIfaceType.setType("simple");
        topIfaceType.setHref("#C2TopType");

        topIface.setType(topIfaceType);

        IInterface bottomIface = types.createInterface();
        bottomIface.setId(id + ".IFACE_BOTTOM");
        bottomIface.setDescription(createDescription(description +
                                                     " Bottom Interface"));
        IDirection bottomDirection = types.createDirection();
        bottomDirection.setValue("inout");
        bottomIface.setDirection(bottomDirection);

        IXMLLink bottomIfaceType = types.createXMLLink();
        bottomIfaceType.setType("simple");
        bottomIfaceType.setHref("#C2BottomType");

        bottomIface.setType(bottomIfaceType);

        c.addInterface(topIface);
        c.addInterface(bottomIface);

        IXMLLink cType = types.createXMLLink();
        cType.setType("simple");
        cType.setHref("#" + typeId);
        c.setType(cType);
        return c;
    }

    protected IConnector createFredConnector(String id,
                                             String description,
                                             String typeId){
        IConnector c = types.createConnector();

        c.setId(id);
        IDescription desc = types.createDescription();
        desc.setValue(description);
        c.setDescription(desc);

        IInterface localIface = types.createInterface();
        localIface.setId(id + ".IFACE_LOCAL");
        localIface.setDescription(createDescription(description +
                                                    " Local Interface"));
        IDirection fredDirection = types.createDirection();
        fredDirection.setValue("inout");
        localIface.setDirection(fredDirection);

        IXMLLink localIfaceType = types.createXMLLink();
        localIfaceType.setType("simple");

        // this is absolute hack....find out another way to do this!!
        String bottomName = "USLocalFredBus";
        String topName = "USDisFredBus";
        if (id.equals(bottomName)){
            localIfaceType.setHref("#C2LocalType");
        } else if (id.equals(topName)){ 
            localIfaceType.setHref("#C2LocalType");
        }

        localIface.setType(localIfaceType);

        c.addInterface(localIface);
        IXMLLink cType = types.createXMLLink();
        cType.setType("simple");
        cType.setHref("#" + typeId);
        c.setType(cType);
        return c;
    }

    protected IComponentType createComponentType(String id,
                                       String description,
                                       String javaClassName,
                                       IInitializationParameter[] initparams) {
        IComponentType ct0 = types.createComponentType();
        IVariantComponentType ct1 =
            variants.promoteToVariantComponentType(ct0);
        IVariantComponentTypeImpl ct =
            implementation.promoteToVariantComponentTypeImpl(ct1);

        ct.setId(id);
        IDescription desc = types.createDescription();
        desc.setValue(description);
        ct.setDescription(desc);

        ISignature topSig = types.createSignature();
        IDescription dSig = types.createDescription();
        dSig.setValue(id + "_topSig");
        topSig.setDescription(dSig);
        IDirection topDirection = types.createDirection();
        topDirection.setValue("inout");
        topSig.setId(id + "_topSig");
        topSig.setDirection(topDirection);

        IXMLLink topSigType = types.createXMLLink();
        topSigType.setType("simple");
        topSigType.setHref("#C2TopType");

        topSig.setType(topSigType);

        ISignature botSig = types.createSignature();
        dSig = types.createDescription();
        dSig.setValue(id + "_bottomSig");
        botSig.setDescription(dSig);
        IDirection botDirection = types.createDirection();
        botDirection.setValue("inout");
        botSig.setId(id + "_bottomSig");
        botSig.setDirection(botDirection);

        IXMLLink botSigType = types.createXMLLink();
        botSigType.setType("simple");
        botSigType.setHref("#C2BottomType");
        botSig.setType(botSigType);

        ct.addSignature(topSig);
        ct.addSignature(botSig);

        IJavaImplementation javaImplementation;

        javaImplementation = createJavaImplementation(javaClassName,
                                                      initparams);
        ct.addImplementation(javaImplementation);

        return ct;
    }

    protected IVariantComponentType createVariantComponentType(
                                                         String id,
                                                         String description) {
        /* Create the initial CT, then promote it to a VCT. */
        IComponentType ctTemp = types.createComponentType();
        IVariantComponentType ct1 =
            variants.promoteToVariantComponentType(ctTemp);
        IVariantComponentTypeImpl ct =
            implementation.promoteToVariantComponentTypeImpl(ct1);

        /* Set the id and description accordingly. */
        ct.setId(id);
        IDescription desc = types.createDescription();
        desc.setValue(description);
        ct.setDescription(desc);

        return ct;
    }

    /**
     * Add a component as a registered variant for this VCT.
     * @param variantType
     * @param idKey
     * @param idValue
     * @param component
     */
    protected void addVariantToVariantComponentType(
                                             IVariantComponentType variantType,
                                             String idKey,
                                             String idValue,
                                             IComponentType component) {

        IXMLLink variantLink = types.createXMLLink();
        variantLink.setType("simple");
        variantLink.setHref("#" + component.getId());

        IVariant variant = variants.createVariant();
        variant.setVariantType(variantLink);

        /* Add this variant to the variant type */
        variantType.addVariant(variant);

        /* Now, here comes the fun part.  Create a boolean guard to
         * represent 'key = "<value>"'
         * The <key> must be surrounded by double quotes for ArchStudio.
         */
        IBooleanGuard guard = boolguards.createBooleanGuard();
        IBooleanExp expr = boolguards.createBooleanExp();
        IEquals equals = boolguards.createEquals();

        ISymbol symbol1 = boolguards.createSymbol();
        symbol1.setValue(idKey);

        IValue value = boolguards.createValue();
        value.setValue('"' + idValue + '"');

        equals.setSymbol(symbol1);
        equals.setValue(value);

        expr.setEquals(equals);
        guard.setBooleanExp(expr);

        variant.setGuard(guard);
    }

    protected IConnectorType createConnectorType(String id,
                                      String description,
                                      String javaClassName,
                                      IInitializationParameter[] initparams) {
        IConnectorType ct0 = types.createConnectorType();
        IVariantConnectorType ct1 = variants.promoteToVariantConnectorType(ct0);
        IVariantConnectorTypeImpl ct =
            implementation.promoteToVariantConnectorTypeImpl(ct1);

        ct.setId(id);
        IDescription desc = types.createDescription();
        desc.setValue(description);
        ct.setDescription(desc);

        ISignature topSig = types.createSignature();
        IDescription dSig = types.createDescription();
        dSig.setValue(id + "_topSig");
        topSig.setDescription(dSig);
        IDirection topDirection = types.createDirection();
        topDirection.setValue("inout");
        topSig.setId(id + "_topSig");
        topSig.setDirection(topDirection);

        IXMLLink topSigType = types.createXMLLink();
        topSigType.setType("simple");
        topSigType.setHref("#C2TopType");

        topSig.setType(topSigType);

        ISignature botSig = types.createSignature();
        dSig = types.createDescription();
        dSig.setValue(id + "_bottomSig");
        botSig.setDescription(dSig);
        IDirection botDirection = types.createDirection();
        botDirection.setValue("inout");
        botSig.setId(id + "_bottomSig");
        botSig.setDirection(botDirection);

        IXMLLink botSigType = types.createXMLLink();
        botSigType.setType("simple");
        botSigType.setHref("#C2BottomType");
        botSig.setType(botSigType);

        ct.addSignature(topSig);
        ct.addSignature(botSig);

        IJavaImplementation javaImplementation;

        javaImplementation = createJavaImplementation(javaClassName,
                                                      initparams);

        ct.addImplementation(javaImplementation);

        return ct;
    }

    protected IConnectorType createFredConnectorType(String id,
                                      String description,
                                      String javaClassName,
                                      IInitializationParameter[] initparams) {
        IConnectorType ct0 = types.createConnectorType();
        IVariantConnectorType ct1 =
            variants.promoteToVariantConnectorType(ct0);
        IVariantConnectorTypeImpl ct =
            implementation.promoteToVariantConnectorTypeImpl(ct1);

        ct.setId(id);
        IDescription desc = types.createDescription();
        desc.setValue(description);
        ct.setDescription(desc);

        ISignature topSig = types.createSignature();
        IDirection topDirection = types.createDirection();
        topDirection.setValue("inout");
        topSig.setDirection(topDirection);

        IXMLLink topSigType = types.createXMLLink();
        topSigType.setType("simple");
        topSigType.setHref("#C2LocalType");

        topSig.setType(topSigType);

        ISignature botSig = types.createSignature();
        IDirection botDirection = types.createDirection();
        botDirection.setValue("inout");
        botSig.setDirection(botDirection);

        IXMLLink botSigType = types.createXMLLink();
        botSigType.setType("simple");
        botSigType.setHref("#C2LocalType");
        botSig.setType(botSigType);

        ct.addSignature(topSig);
        ct.addSignature(botSig);

        IJavaImplementation javaImplementation;

        javaImplementation = createJavaImplementation(javaClassName,
                                                      initparams);

        ct.addImplementation(javaImplementation);

        return ct;
    }

    public INamedProperty createNamedProperty(String name, String value) {
        INamedProperty npRef = messages.createNamedProperty();
        IPropertyName nameRef = messages.createPropertyName();

        nameRef.setValue(name);
        IPropertyValue valueRef = messages.createPropertyValue();
        valueRef.setValue(value);
        npRef.setName(nameRef);
        npRef.setValue(valueRef);

        return npRef;
    }

    public INamedProperty copyNamedProperty(INamedProperty npRef) {
        String name = (String)((IPropertyName)npRef.getName()).getValue();
        String value = (String)((IPropertyValue)npRef.getValue()).getValue();
        return createNamedProperty(name, value);
    }

    public INamedPropertyMessage createMessage(String id, String kind,
                                               String description,
                                               String count, String name,
                                               String type) {
        INamedPropertyMessage messageRef =
            messages.createNamedPropertyMessage();
        IDescription descriptionRef = createDescription(description);
        ICount countRef = messages.createCount();
        countRef.setValue(count);
        IMessageName nameRef = messages.createMessageName();
        nameRef.setValue(name);
        IMessageType typeRef = messages.createMessageType();
        typeRef.setValue(type);

        messageRef.setId(id);
        messageRef.setDescription(descriptionRef);
        messageRef.setKind(kind);
        messageRef.setCount(countRef);
        messageRef.setName(nameRef);
        messageRef.setType(typeRef);

        return messageRef;
    }

    public INamedPropertyMessage copyMessage(INamedPropertyMessage messageRef) {
        String id = (String)messageRef.getId();
        IDescription idescription = messageRef.getDescription();
        String description = (String)idescription.getValue();
        String kind = (String)messageRef.getKind();
        ICount icount = messageRef.getCount();
        String count = (String)icount.getValue();
        IMessageName iname = messageRef.getName();
        String name = (String)iname.getValue();
        IMessageType itype = messageRef.getType();
        String type = (String)itype.getValue();
        Collection npRef = messageRef.getAllNamedPropertys();
        INamedProperty namedProperties[] = new INamedProperty[npRef.size()];
        int i = 0;
        for (Iterator it = npRef.iterator(); it.hasNext(); ) {
            namedProperties[i++] =
                copyNamedProperty((INamedProperty)(it.next()));
        }

        return createMessage(id, kind, description, count, name, type,
                             namedProperties);
    }

    public INamedPropertyMessage createMessage(String id, String kind,
                                               String description,
                                               String count, String name,
                                               String type,
                                               INamedProperty np[]) {
        INamedPropertyMessage npmRef =
            createMessage(id, kind, description, count, name, type);
        for (int i = 0; i < np.length; i++ )
            npmRef.addNamedProperty(np[i]);

        return npmRef;
    }

    public IProductionRule createRule(String id, String description,
                                      INamedPropertyMessage recvMsg[],
                                      INamedPropertyMessage sendMsg[],
                                      String causeTime) {
        IProductionRule ruleRef = messages.createProductionRule();
        IDescription descriptionRef = createDescription(description);
        ICauseTime causeTimeRef = messages.createCauseTime();
        causeTimeRef.setValue(causeTime);

        ruleRef.setId(id);
        ruleRef.setDescription(descriptionRef);
        for (int i = 0; i < recvMsg.length; i++)
            ruleRef.addReceiveMessage(copyMessage(recvMsg[i]));
        for (int i = 0; i < sendMsg.length; i++)
            ruleRef.addSendMessage(copyMessage(sendMsg[i]));
        ruleRef.setCauseTime(causeTimeRef);
        return ruleRef;
    }

    private IMessageCausalitySpecification getMCS(String ruleNames[]) {
        IMessageCausalitySpecification mcsRef =
            messages.createMessageCausalitySpecification();
        for (int i = 0; i < ruleNames.length; i++){
            IXMLLink ruleRef = types.createXMLLink();
            ruleRef.setType("simple");
            ruleRef.setHref("#"+ruleNames[i]);
            mcsRef.addRule(ruleRef);
        }
        return mcsRef;
    }

    public IComponentType addRulesComp(String ruleNames[],
                                               IComponentType ct0) {
        IVariantComponentTypeImplVers ct1 =
            versions.promoteToVariantComponentTypeImplVers
            ((IVariantComponentTypeImpl)ct0);

        IVariantComponentTypeImplVersSpec ct =
            messages.promoteToVariantComponentTypeImplVersSpec(ct1);
        ct.setMessageCausalitySpecification(getMCS(ruleNames));
        return ct;
    }

    public IConnectorType addRulesConn(String ruleNames[],
                                       IConnectorType ct0) {
        IVariantConnectorTypeImplVers ct1 =
            versions.promoteToVariantConnectorTypeImplVers
            ((IVariantConnectorTypeImpl)ct0);
        IVariantConnectorTypeImplVersSpec ct =
            messages.promoteToVariantConnectorTypeImplVersSpec(ct1);
        ct.setMessageCausalitySpecification(getMCS(ruleNames));
        return ct;
    }

    /**
     * Create the hardcoded architecture.
     */
    public void createArchitecture(String name) {
        /* Create the types. */
        IArchTypes archTypes = types.createArchTypesElement();
        xArch.addObject(archTypes);

        createInterfaceTypes(archTypes);
        createConnectorTypes(archTypes);
        createComponentTypes(archTypes);

        /* Create the basic architecture. */
        IArchStructure archStructure = types.createArchStructureElement();
        archStructure.setId(name);
        archStructure.
            setDescription(createDescription(name));
        xArch.addObject(archStructure);

        createComponents(archStructure);
        createConnectors(archStructure);
        createLinks(archStructure);

        /* Add rules */
        createRules(archStructure);
    }

    /**
     * Register all of the interface types.
     * @param archTypes
     */
    public void createInterfaceTypes(IArchTypes archTypes) {
    }

    /**
     * Register all of the connector types.
     * @param archTypes
     */
    public void createConnectorTypes(IArchTypes archTypes) {
    }

    /**
     * Register all of the component types.
     * @param archTypes
     */
    public void createComponentTypes(IArchTypes archTypes) {
    }

    /**
     * Create components.
     */
    public void createComponents(IArchStructure archStructure) {
    }

    /**
     * Create the connectors in the architecture.
     */
    public void createConnectors(IArchStructure archStructure) {
    }

    /**
     * Create the links in the architecture.
     */
    public void createLinks(IArchStructure archStructure) {
    }

    /**
     * Create the rules for the architecture.
     */
    public void createRules(IArchStructure archStructure) {
    }

    /**
     * Return a 'pretty' XML representation of the architecture.
     */
    public String getXml() {
        return XArchUtils.getPrettyXmlRepresentation(xArch);
    }

    public static void main(String[] args){
        /* Current released versions of Ant place spurious new lines
         * when using the Java taskdef with output parameter, so we
         * support printing to a file listed in args[0] to work properly.
         */
        Description d = new Description();
        d.createArchitecture("Sample");

        if (args.length > 0) {
            try {
                FileOutputStream fos = new FileOutputStream(args[0]);
                PrintStream ps = new PrintStream(fos);
                ps.println(d.getXml());
            }
            catch (java.io.FileNotFoundException fnfe) {
                System.err.println(fnfe);
            }
        }
        else {
            System.out.println(d.getXml());
        }
    }

}
