/*
 * Copyright (c) 2002-2012, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.workflow.modules.previewnotification.web;

import fr.paris.lutece.plugins.workflow.modules.previewnotification.service.IPreviewNotificationService;
import fr.paris.lutece.plugins.workflow.service.taskinfo.TaskInfoManager;
import fr.paris.lutece.plugins.workflowcore.business.action.Action;
import fr.paris.lutece.plugins.workflowcore.business.config.ITaskConfig;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistory;
import fr.paris.lutece.plugins.workflowcore.service.action.IActionService;
import fr.paris.lutece.plugins.workflowcore.service.resource.IResourceHistoryService;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.plugins.workflowcore.web.task.TaskComponent;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.html.HtmlTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;


/**
 *
 * PreviewNotificationTaskComponent
 *
 */
public class PreviewNotificationTaskComponent extends TaskComponent
{
    private static final String SEPARATOR = ";";

    // MARK
    public static final String MARK_TASK_MESSAGE = "task_message_";
    public static final String MARK_TASK_TITLE = "task_title_";
    public static final String MARK_TASK = "task";
    public static final String MARK_LIST_ID_TASK = "list_id_task";

    // PROPERTIES
    private static final String PROPERTIE_ACCEPTED_TASK_TYPE_KEY = "workflow-previewnotification.acceptedTaskTypeKey";

    // TEMPLATES
    /** The Constant TEMPLATE_TASK_FORM. */
    private static final String TEMPLATE_TASK_FORM = "admin/plugins/workflow/modules/previewnotification/task_preview_notification_form.html";

    // SERVICES
    @Inject
    private IPreviewNotificationService _previewNotificationService;
    @Inject
    private IResourceHistoryService _resourceHistoryService;
    @Inject
    private IActionService _actionService;

    @Override
    public String getDisplayTaskForm( int nIdResource, String strResourceType, HttpServletRequest request,
        Locale locale, ITask task )
    {
        Map<String, Object> model = new HashMap<String, Object>(  );

        String strListAcceptedTaskTypeKey = AppPropertiesService.getProperty( PROPERTIE_ACCEPTED_TASK_TYPE_KEY );

        String[] listAcceptedTaskTypeKey = strListAcceptedTaskTypeKey.split( SEPARATOR );
        List<Integer> listTaskId = new ArrayList<Integer>(  );
        Map<String, String> taskInfo = new HashMap<String, String>(  );
        Action action = _actionService.findByPrimaryKey( task.getAction( ).getId( ) );

        // Fill the model with the info of other tasks
        for ( ITask otherTask : this._previewNotificationService.getListTasks( task.getAction(  ).getId(  ), locale ) )
        {
            boolean autorizedType = false;

            for ( String acceptedTaskTypeKey : listAcceptedTaskTypeKey )
            {
                if ( otherTask.getTaskType(  ).getKey(  ).equals( acceptedTaskTypeKey ) )
                {
                    autorizedType = true;
                }
            }

            if ( autorizedType )
            {
                ResourceHistory nIdHistory = _resourceHistoryService.getLastHistoryResource( nIdResource,
                        strResourceType, action.getWorkflow( ).getId( ) );
                taskInfo.put( MARK_TASK_MESSAGE + otherTask.getId(  ),
                        TaskInfoManager.getTaskResourceInfo( nIdHistory.getId( ), otherTask.getId( ), request ) );
                taskInfo.put( MARK_TASK_TITLE + otherTask.getId(  ), otherTask.getTaskType(  ).getTitle(  ) );
                listTaskId.add( otherTask.getId(  ) );
            }

            autorizedType = false;
        }

        model.put( MARK_LIST_ID_TASK, listTaskId );
        model.put( MARK_TASK, taskInfo );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_TASK_FORM, locale, model );

        return template.getHtml(  );
    }

    @Override
    public String getDisplayConfigForm( HttpServletRequest request, Locale locale, ITask task )
    {
        return null;
    }

    @Override
    public String getDisplayTaskInformation( int nIdHistory, HttpServletRequest request, Locale locale, ITask task )
    {
        return null;
    }

    @Override
    public String getTaskInformationXml( int nIdHistory, HttpServletRequest request, Locale locale, ITask task )
    {
        return null;
    }

    @Override
    public String doValidateTask( int nIdResource, String strResourceType, HttpServletRequest request, Locale locale,
        ITask task )
    {
        return null;
    }

    @Override
    public String validateConfig( ITaskConfig config, HttpServletRequest request )
    {
        return null;
    }
}
