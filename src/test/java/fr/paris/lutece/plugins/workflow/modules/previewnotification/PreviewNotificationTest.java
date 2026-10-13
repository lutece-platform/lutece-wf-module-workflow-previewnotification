/*
 * Copyright (c) 2002-2026, Mairie de Paris
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
package fr.paris.lutece.plugins.workflow.modules.previewnotification;

import java.util.Locale;

import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.workflow.modules.previewnotification.service.IPreviewNotificationService;
import fr.paris.lutece.plugins.workflow.modules.previewnotification.service.TaskPreviewNotification;
import fr.paris.lutece.plugins.workflow.modules.previewnotification.service.TaskTypePreviewNotificationProducer;
import fr.paris.lutece.plugins.workflowcore.business.task.ITaskType;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.plugins.workflowcore.web.task.ITaskComponent;
import fr.paris.lutece.test.LuteceTestCase;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.enterprise.inject.literal.NamedLiteral;

/**
 * Tests the CDI wiring of the preview notification task.
 */
public class PreviewNotificationTest extends LuteceTestCase
{
    private static final String TASK_TYPE_KEY = "taskPreviewNotification";

    @Inject
    @Named( TaskTypePreviewNotificationProducer.BEAN_TASK_TYPE )
    private ITaskType _taskType;

    @Inject
    @Named( "workflow-previewnotification.previewNotificationTaskComponent" )
    private ITaskComponent _taskComponent;

    @Inject
    private IPreviewNotificationService _previewNotificationService;

    /**
     * The task type is built from the plugin properties.
     */
    @Test
    public void testTaskType( )
    {
        assertEquals( TASK_TYPE_KEY, _taskType.getKey( ) );
        assertEquals( TaskPreviewNotification.BEAN_TASK, _taskType.getBeanName( ) );
        assertFalse( _taskType.isConfigRequired( ) );
        assertTrue( _taskType.isFormTaskRequired( ) );
        assertFalse( _taskType.isTaskForAutomaticAction( ) );
    }

    /**
     * The task bean resolves by the name the task type declares, one instance per lookup.
     */
    @Test
    public void testTaskBean( )
    {
        ITask task = CDI.current( ).select( ITask.class, NamedLiteral.of( _taskType.getBeanName( ) ) ).get( );
        ITask other = CDI.current( ).select( ITask.class, NamedLiteral.of( _taskType.getBeanName( ) ) ).get( );

        assertTrue( task instanceof TaskPreviewNotification );
        assertNotSame( task, other );
        assertEquals( "Preview notification", task.getTitle( Locale.ENGLISH ) );
    }

    /**
     * The task component answers for the preview notification task type only.
     */
    @Test
    public void testTaskComponent( )
    {
        assertTrue( _taskComponent.isInvoked( TASK_TYPE_KEY ) );
        assertFalse( _taskComponent.isInvoked( "taskNotification" ) );
    }

    /**
     * An action without task gives an empty list.
     */
    @Test
    public void testListTasksOfUnknownAction( )
    {
        assertTrue( _previewNotificationService.getListTasks( Integer.MAX_VALUE, Locale.ENGLISH ).isEmpty( ) );
    }
}
