/*******************************************************************************
 * Copyright (c) 2004 Actuate Corporation .
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Actuate Corporation  - initial API and implementation
 *******************************************************************************/

package org.eclipse.birt.report.designer.internal.ui.editors.schematic.extensions;

import java.util.Iterator;
import java.util.List;

import org.eclipse.birt.report.designer.core.IReportElementConstants;
import org.eclipse.birt.report.designer.internal.ui.extension.ExtendedEditPart;
import org.eclipse.birt.report.designer.internal.ui.extension.ExtendedElementUIPoint;
import org.eclipse.birt.report.designer.internal.ui.extension.ExtensionPointManager;
import org.eclipse.birt.report.designer.internal.ui.extension.IExtensionConstants;
import org.eclipse.birt.report.designer.internal.ui.palette.PaletteCategory;
import org.eclipse.birt.report.designer.internal.ui.palette.ReportCombinedTemplateCreationEntry;
import org.eclipse.birt.report.designer.internal.ui.palette.ReportElementFactory;
import org.eclipse.birt.report.designer.nls.Messages;
import org.eclipse.birt.report.designer.ui.extensions.IReportItemPropertyEditUI;
import org.eclipse.birt.report.designer.ui.extensions.IReportItemUI;
import org.eclipse.birt.report.designer.ui.views.attributes.DefaultPageGenerator;
import org.eclipse.birt.report.model.api.DesignEngine;
import org.eclipse.birt.report.model.api.ExtendedItemHandle;
import org.eclipse.gef.palette.CombinedTemplateCreationEntry;
import org.eclipse.gef.palette.PaletteContainer;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.jface.resource.ImageDescriptor;

/**
 * Deal with the extension element
 *  
 */
public class GuiExtensionManager
{

	public static final String PALETTE_DESIGNER = "pallet_designer";
	public static final String PALETTE_MASTERPAGE = "pallet_masterpage";

	public static final String DESIGNER_FACTORY = "designer_factory"; //$NON-NLS-1$
	public static final String ATTRIBUTE = "attribute"; //$NON-NLS-1$

	private static final String EXT_MGR_LABEL = Messages.getString( "GuiExtensionManager.label.name" );

	/**
	 * @param extension
	 * @param object
	 * @return
	 */
	public static Object doExtension( IExtension extension, Object object )
	{
		List list = ExtensionPointManager.getInstance( )
				.getExtendedElementPoints( );
		if ( list == null || list.size( ) == 0 )
		{
			return null;
		}
		Object retValue = null;
		if ( PALETTE_DESIGNER.equals( extension.getExtendsionIdentify( ) )
				|| PALETTE_MASTERPAGE.equals( extension.getExtendsionIdentify( ) ) )
		{

			retValue = doPalette( object, extension.getExtendsionIdentify( ) );
		}
		else if ( DESIGNER_FACTORY.equals( extension.getExtendsionIdentify( ) ) )
		{
			retValue = doDesignerFactory( object );
		}
		else if ( ATTRIBUTE.equals( extension.getExtendsionIdentify( ) ) )
		{
			retValue = doAttribute( object );
		}

		return retValue;
	}

	private static Object doAttribute( Object object )
	{
		Object retValue = null;
		if ( object instanceof ExtendedItemHandle )
		{
			retValue = new DefaultPageGenerator( );
			ExtendedItemHandle handle = (ExtendedItemHandle) object;
			String id = getExtendedElementID( handle );
			ExtendedElementUIPoint point = ExtensionPointManager.getInstance( )
					.getExtendedElementPoint( id );
			if ( point == null )
			{
				return retValue;
			}
			IReportItemPropertyEditUI edit = point.getReportItemPropertyEditUI( );
			if ( edit == null )
			{
				return retValue;
			}

			ExtendedItemPageGenerator generator = new ExtendedItemPageGenerator( );
			generator.setQuickEdit( edit );
			retValue = generator;
		}
		return retValue;
	}

	private static Object doDesignerFactory( Object object )
	{
		if ( object instanceof ExtendedItemHandle )
		{
			ExtendedItemHandle model = (ExtendedItemHandle) object;
			ExtendedEditPart part = new ExtendedEditPart( model );
			String id = getExtendedElementID( model );

			ExtendedElementUIPoint point = ExtensionPointManager.getInstance( )
					.getExtendedElementPoint( id );
			if ( point == null )
			{
				return null;
			}
			IReportItemUI UI = ExtensionPointManager.getInstance( )
					.getExtendedElementPoint( id )
					.getReportItemUI( );
			if ( UI == null )
			{
				return null;
			}
			part.setExtendedElementUI( UI );
			return part;
		}
		return null;
	}

	/**
	 * @param model
	 * @return
	 */
	public static String getExtendedElementID( ExtendedItemHandle model )
	{
		return model.getExtensionName( );
	}

	/**
	 * Get display name
	 * 
	 * @param obj
	 * @return
	 */
	public static String getExtensionDisplayName( Object obj )
	{
		String value = EXT_MGR_LABEL;
		if ( obj instanceof ExtendedItemHandle )
		{
			String name = ( (ExtendedItemHandle) obj ).getDefn( )
					.getDisplayName( );
			if ( name != null )
			{
				value = name;
			}
		}

		return value;
	}

	/**
	 * @param object
	 */
	private static Object doPalette( Object object, String type )
	{
		assert ( object instanceof PaletteRoot );
		PaletteRoot root = (PaletteRoot) object;
		List list = root.getChildren( );
		List exts = ExtensionPointManager.getInstance( )
				.getExtendedElementPoints( );

		if ( exts == null )
		{
			return root;
		}

		for ( Iterator itor = exts.iterator( ); itor.hasNext( ); )
		{
			ExtendedElementUIPoint point = (ExtendedElementUIPoint) itor.next( );
			if ( point == null )
			{
				return root;
			}
			String category = (String) point.getAttribute( IExtensionConstants.PALETTE_CATEGORY );

			ImageDescriptor icon = (ImageDescriptor) point.getAttribute( IExtensionConstants.PALETTE_ICON );

			IReportItemUI UI = point.getReportItemUI( );
			if ( UI == null )
			{
				return root;
			}

			if ( PALETTE_DESIGNER.equals( type ) )
			{
				Boolean bool = (Boolean) point.getAttribute( IExtensionConstants.EDITOR_SHOW_IN_DESIGNER );
				if ( !bool.booleanValue( ) )
				{
					continue;
				}
			}
			else if ( PALETTE_MASTERPAGE.equals( type ) )
			{
				Boolean bool = (Boolean) point.getAttribute( IExtensionConstants.EDITOR_SHOW_IN_MASTERPAGE );
				if ( !bool.booleanValue( ) )
				{
					continue;
				}
			}
			String displayName = DesignEngine.getMetaDataDictionary( )
					.getExtension( point.getExtensionName( ) )
					.getDisplayName( );
			CombinedTemplateCreationEntry combined = new ReportCombinedTemplateCreationEntry( displayName,
					displayName,
					IReportElementConstants.REPORT_ELEMENT_EXTENDED
							+ point.getExtensionName( ),
					new ReportElementFactory( IReportElementConstants.REPORT_ELEMENT_EXTENDED
							+ point.getExtensionName( ) ),
					icon,
					icon,
					new ExtendedElementToolExtends( point.getExtensionName( ) ) );
			PaletteContainer entry = findCategory( list, category );
			if ( entry == null )
			{
				String categoryLabel = (String) point.getAttribute( IExtensionConstants.PALETTE_CATEGORY_DISPLAYNAME );
				if ( categoryLabel == null )
				{
					categoryLabel = category;
				}
				entry = new PaletteCategory( category, categoryLabel, null );
				root.add( entry );
			}
			entry.add( combined );
		}
		return root;
	}

	private static PaletteCategory findCategory( List list, String category )
	{
		for ( Iterator itor = list.iterator( ); itor.hasNext( ); )
		{
			Object entry = itor.next( );
			if ( entry instanceof PaletteCategory )
			{
				if ( ( (PaletteCategory) entry ).getCategoryName( )
						.equals( category ) )
				{
					return (PaletteCategory) entry;
				}
			}
		}
		return null;
	}

}