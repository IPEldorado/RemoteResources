/*
 * (C) Copyright 2014 Instituto de Pesquisas Eldorado (http://www.eldorado.org.br/).
 *
 * This file is part of the software Remote Resources
 *
 * All rights reserved. This file and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 3.0 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 *
 * This file is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 */

package com.eldorado.remoteresources.ui.wizard;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

/**
 * This class is responsible for execute several tasks, notifying its progress
 * 
 * @author Marcelo Marzola Bossoni
 * 
 */
class WorkerRunnable extends Thread implements PropertyChangeListener {

	private final List<SwingWorker<Boolean, Void>> workers;

	private final JProgressBar progressBar;

	private SwingWorker<Boolean, Void> currentWorker;

	private final Thread onFinishThread;

	private int progressPerWorker = 100;

	private int numberOfWorkers = 0;

	private int currentWorkerCounter = 0;

	private boolean successfullyFinished = true;

	/**
	 * Create a new runnable with desired tasks
	 * 
	 * @param workers
	 *            the workers
	 * @param progressBar
	 *            the progress to update
	 * @param onFinishThread
	 *            the thread to be executed after all workers had been executed
	 */
	public WorkerRunnable(List<SwingWorker<Boolean, Void>> workers,
			JProgressBar progressBar, Thread onFinishThread) {
		this.workers = workers;
		this.progressBar = progressBar;
		this.onFinishThread = onFinishThread;
	}

	@Override
	public void run() {
		progressBar.setMinimum(0);
		progressBar.setMaximum(100);
		progressBar.setValue(0);
		progressBar.setStringPainted(true);
		progressPerWorker = (int) Math.floor(100 / workers.size());
		numberOfWorkers = workers.size();

		for (SwingWorker<Boolean, Void> worker : workers) {
			synchronized (this) {
				currentWorker = worker;
			}
			worker.addPropertyChangeListener(this);
			worker.execute();
			while (!worker.isDone() && !worker.isCancelled()) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// do nothing
				}
			}
			try {
				successfullyFinished = successfullyFinished && worker.get();
			} catch (InterruptedException e) {
				// do nothing
			} catch (ExecutionException e) {
				successfullyFinished = false;
			}
			currentWorkerCounter++;
		}
		if (onFinishThread != null) {
			onFinishThread.start();
		}

	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		synchronized (this) {
			progressBar.setValue((currentWorkerCounter * progressPerWorker)
					+ (currentWorker.getProgress() / numberOfWorkers));
		}
	}

	/**
	 * Get if all workers were successfully executed
	 * 
	 * @return true if all workers were successfully executed, false otherwise.
	 */
	public boolean wasSuccessfullyFinished() {
		return successfullyFinished;
	}
}