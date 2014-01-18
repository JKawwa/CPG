//
//  TCPAppDelegate.h
//  HostTCP
//
//  Created by Jad Kawwa on 2013-09-21.
//  Copyright (c) 2013 Jad Kawwa. All rights reserved.
//

#import <Cocoa/Cocoa.h>

@interface TCPAppDelegate : NSObject <NSApplicationDelegate>

@property (assign) IBOutlet NSWindow *window;

@property (readonly, strong, nonatomic) NSPersistentStoreCoordinator *persistentStoreCoordinator;
@property (readonly, strong, nonatomic) NSManagedObjectModel *managedObjectModel;
@property (readonly, strong, nonatomic) NSManagedObjectContext *managedObjectContext;

- (IBAction)saveAction:(id)sender;

@end
