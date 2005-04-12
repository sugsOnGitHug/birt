/*******************************************************************************
 * Copyright (c) 2004 Actuate Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Actuate Corporation  - initial API and implementation
 *******************************************************************************/

package org.eclipse.birt.report.designer.internal.ui.editors.schematic.tools;

import org.eclipse.birt.report.designer.core.DesignerConstants;
import org.eclipse.birt.report.designer.core.IReportElementConstants;
import org.eclipse.birt.report.designer.core.model.SessionHandleAdapter;
import org.eclipse.birt.report.designer.internal.ui.dnd.DNDUtil;
import org.eclipse.birt.report.designer.internal.ui.editors.schematic.editparts.LabelEditPart;
import org.eclipse.birt.report.designer.nls.Messages;
import org.eclipse.birt.report.model.api.DataItemHandle;
import org.eclipse.birt.report.model.api.LabelHandle;
import org.eclipse.birt.report.model.api.TextItemHandle;
import org.eclipse.birt.report.model.api.elements.ReportDesignConstants;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.requests.CreationFactory;
import org.eclipse.gef.tools.CreationTool;
import org.eclipse.swt.widgets.Display;

/**
 * Customizes eclipse creation tool to popup element builders.
 */
public class ReportCreationTool extends CreationTool
{

	private static final String MODEL_CREATE_ELEMENT_TRANS = Messages.getString( "ReportCreationTool.ModelTrans.CreateElement" ); //$NON-NLS-1$

	private AbstractToolHandleExtends preHandle;

	/**
	 * Constructor
	 * 
	 * @param factory
	 * @param preHandle
	 */
	public ReportCreationTool( CreationFactory factory,
			AbstractToolHandleExtends preHandle )
	{
		super( factory );
		this.preHandle = preHandle;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.tools.CreationTool#performCreation(int)
	 */
	protected void performCreation( int button )
	{
		SessionHandleAdapter.getInstance( )
				.getReportDesignHandle( )
				.getCommandStack( )
				.startTrans( MODEL_CREATE_ELEMENT_TRANS );
		if ( getTargetEditPart( ) != null )
		{
			if ( preHandle != null )
			{
				preHandle.setRequest( this.getCreateRequest( ) );
				preHandle.setTargetEditPart( getTargetEditPart( ) );

				Command command = getCurrentCommand( );
				if ( command != null && command.canExecute( ) )
				{
					if ( !preHandle.preHandleMouseUp( ) )
					{
						//if a popup dialog was cancelled.
						//All create logic should be finished there.
						SessionHandleAdapter.getInstance( )
								.getReportDesignHandle( )
								.getCommandStack( )
								.rollback( );
						handleFinished( );
						return;
					}
				}
			}
		}

		super.performCreation( button );
		SessionHandleAdapter.getInstance( )
				.getReportDesignHandle( )
				.getCommandStack( )
				.commit( );
		selectAddedObject( );

	}

	/**
	 * Performs the creation. Runs the creation via simulating the mouse move event.
	 * 
	 * @param editPart
	 *            the current EditPart
	 */
	public void performCreation( EditPart editPart )
	{
		if ( editPart == null )
			return;
		setTargetEditPart( editPart );
		boolean validateCurr = handleValidatePalette( getFactory( ).getObjectType( ),
				getTargetEditPart( ) );
		if ( !validateCurr )
		{
			//Validates the parent part
			setTargetEditPart( editPart.getParent( ) );
		}
		if ( validateCurr
				|| handleValidatePalette( getFactory( ).getObjectType( ),
						getTargetEditPart( ) ) )
		{
			//Sets the insertion point
			IFigure figure = ( (GraphicalEditPart) editPart ).getFigure( );
			Rectangle rect = figure.getBounds( ).getCopy( );
			figure.translateToAbsolute( rect );
			Point point = rect.getRight( );
			point.performTranslate( 1, 1 );
			getCreateRequest( ).setLocation( point );

			setCurrentCommand( getCommand( ) );
			performCreation( MOUSE_BUTTON1 );
		}
		eraseTargetFeedback( );
	}

	/*
	 * Add the newly created object to the viewer's selected objects.
	 */
	private void selectAddedObject( )
	{
		final Object model = getCreateRequest( ).getExtendedData( )
				.get( DesignerConstants.KEY_NEWOBJECT );
		final EditPartViewer viewer = getCurrentViewer( );
		selectAddedObject( model, viewer );
	}

	/**
	 * Selects or clicks added object
	 * 
	 * @param model
	 *            new object, null will do nothing
	 * @param viewer
	 *            edit part viewer, null will do nothing
	 */
	public static void selectAddedObject( final Object model,
			final EditPartViewer viewer )
	{
		if ( model == null || viewer == null )
			return;

		Display.getCurrent( ).asyncExec( new Runnable( ) {

			public void run( )
			{
				Object editpart = viewer.getEditPartRegistry( ).get( model );

				if ( editpart instanceof EditPart )
				{
					viewer.flush( );
					viewer.select( (EditPart) editpart );
				}
				if ( editpart instanceof LabelEditPart )
				{
					Object handle = ( (LabelEditPart) editpart ).getModel( );
					if ( handle instanceof TextItemHandle )
					{
						if ( ( (TextItemHandle) handle ).getContent( ) != null )
							return;
					}
					else if ( handle instanceof DataItemHandle )
					{
						if ( ( (DataItemHandle) handle ).getValueExpr( ) != null )
							return;
					}
					else if ( handle instanceof LabelHandle )
					{
						if ( ( (LabelHandle) handle ).getText( ) != null )
							return;
					}

					( (LabelEditPart) editpart ).performDirectEdit( );
				}
				viewer.reveal( (EditPart) editpart );
			}
		} );
	}

	protected static String getCreationType( String template )
	{
		String type = ""; //$NON-NLS-1$
		if ( IReportElementConstants.REPORT_ELEMENT_IMAGE.equalsIgnoreCase( template ) )
		{
			type = ReportDesignConstants.IMAGE_ITEM;
		}
		else if ( IReportElementConstants.REPORT_ELEMENT_TABLE.equalsIgnoreCase( template ) )
		{
			type = ReportDesignConstants.TABLE_ITEM;
		}
		else if ( IReportElementConstants.REPORT_ELEMENT_TEXT.equalsIgnoreCase( template )
				|| IReportElementConstants.AUTOTEXT_PAGE.equalsIgnoreCase( template )
				|| IReportElementConstants.AUTOTEXT_DATE.equalsIgnoreCase( template )
				|| IReportElementConstants.AUTOTEXT_CREATEDON.equalsIgnoreCase( template )
				|| IReportElementConstants.AUTOTEXT_CREATEDBY.equalsIgnoreCase( template )
				|| IReportElementConstants.AUTOTEXT_FILENAME.equalsIgnoreCase( template )
				|| IReportElementConstants.AUTOTEXT_LASTPRINTED.equalsIgnoreCase( template )
				|| IReportElementConstants.AUTOTEXT_PAGEXOFY.equalsIgnoreCase( template ) )
		{
			type = ReportDesignConstants.TEXT_ITEM;
		}
		else if ( IReportElementConstants.AUTOTEXT_AUTHOR_PAGE_DATE.equalsIgnoreCase( template )
				|| IReportElementConstants.AUTOTEXT_CONFIDENTIAL_PAGE.equalsIgnoreCase( template )
				|| IReportElementConstants.REPORT_ELEMENT_GRID.equalsIgnoreCase( template ) )
		{
			type = ReportDesignConstants.GRID_ITEM;
		}
		else if ( IReportElementConstants.REPORT_ELEMENT_LABEL.equalsIgnoreCase( template ) )
		{
			type = ReportDesignConstants.LABEL_ITEM;
		}
		else if ( IReportElementConstants.REPORT_ELEMENT_DATA.equalsIgnoreCase( template ) )
		{
			type = ReportDesignConstants.DATA_ITEM;
		}
		else if ( IReportElementConstants.REPORT_ELEMENT_LIST.equalsIgnoreCase( template ) )
		{
			type = ReportDesignConstants.LIST_ITEM;
		}
		else
		{
			if ( template.startsWith( IReportElementConstants.REPORT_ELEMENT_EXTENDED ) )
			{
				type = ReportDesignConstants.EXTENDED_ITEM;
			}
		}
		return type;
	}

	/**
	 * Validates specified creation type can be inserted to layout editor.
	 * 
	 * @param objectType
	 *            specified creation type
	 * @param targetEditPart
	 * @return validate result
	 */
	public static boolean handleValidatePalette( Object objectType,
			EditPart targetEditPart )
	{
		return objectType instanceof String
				&& targetEditPart != null
				&& DNDUtil.handleValidateTargetCanContainType( targetEditPart.getModel( ),
						ReportCreationTool.getCreationType( (String) objectType ) )
				&& DNDUtil.handleValidateTargetCanContainMore( targetEditPart.getModel( ),
						1 );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.tools.CreationTool#handleMove()
	 */
	protected boolean handleMove( )
	{
		boolean validateTrue = false;
		updateTargetUnderMouse( );
		if ( getTargetEditPart( ) != null )
		{
			validateTrue = handleValidatePalette( getFactory( ).getObjectType( ),
					getTargetEditPart( ) );
		}

		if ( validateTrue )
		{
			updateTargetRequest( );
			setCurrentCommand( getCommand( ) );
			showTargetFeedback( );
		}
		else
		{
			setCurrentCommand( null );
		}
		return validateTrue;
	}
}